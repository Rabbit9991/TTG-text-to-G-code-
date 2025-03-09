package com.example.testapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.util.retry.Retry;

import java.time.Duration;



@Service
public class MeshapiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //api.key는 application.properties에 있음 수정
    @Value("${meshy.api.key}")
    private String apiKey;

    public MeshapiService(WebClient webClient) {

        this.webClient = webClient;
    }

    //프롬프트만들기
    public Mono<String> generate3DText(String mode, String prompt, String artStyle) {
        String url = "https://api.meshy.ai/v2/text-to-3d";

        String requestBody = String.format("""
        {
            "mode": "%s",
            "prompt": "%s",
            "art_style": "%s",
            "negative_prompt": "low quality, low resolution, low poly, ugly"
        }
        """, mode, prompt, artStyle);

        //해당 프롬프트 api로 전송
        return this.webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        return jsonNode.get("result").asText();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse JSON response", e);
                    }
                });
    }

    public Mono<TextTo3DResponse> fetchResult(String resultId) {
        String url = "https://api.meshy.ai/v2/text-to-3d/" + resultId;

        return this.webClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                //503에러가 자꾸 발생해서 예외처리 해둠 --> 503에러가 나오지 않게 로직 변경
                .onStatus(status -> status.value() == 503, clientResponse -> Mono.error(new RuntimeException("Service Unavailable")))
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        return Mono.just(objectMapper.readValue(response, TextTo3DResponse.class));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to parse JSON response", e));
                    }
                 });
    }
}
