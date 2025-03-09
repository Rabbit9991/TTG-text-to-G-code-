package com.example.testapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Meshapicontroller{

    private final MeshapiService meshapiService;

    public Meshapicontroller(MeshapiService meshapiService) {
        this.meshapiService = meshapiService;
    }

    //해당 주소로 값들을 받아와서 generate3DText 함수 실행
    @GetMapping("/generate-3d-text")
    public Mono<String> generate3DText(
            @RequestParam String mode,
            @RequestParam String prompt,
            @RequestParam("art_style") String artStyle) {
        return meshapiService.generate3DText(mode, prompt, artStyle)
                .map(resultId -> "{ \"result\": \"" + resultId + "\" }");
    }

    //모델 데이터 생성
    @GetMapping("/fetch-result")
    public Mono<TextTo3DResponse> fetchResult(@RequestParam String resultId) {
        return meshapiService.fetchResult(resultId);
    }
}
