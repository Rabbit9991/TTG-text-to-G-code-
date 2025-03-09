package com.example.testapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextTo3DResponse {

    private String id;

    @JsonProperty("model_urls")
    private Map<String, String> modelUrls;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    private String prompt;

    @JsonProperty("art_style")
    private String artStyle;

    @JsonProperty("negative_prompt")
    private String negativePrompt;

    private int progress;

    @JsonProperty("started_at")
    private long startedAt;

    @JsonProperty("created_at")
    private long createdAt;

    @JsonProperty("finished_at")
    private long finishedAt;

    private String status;

    @JsonProperty("texture_urls")
    private List<TextureUrl> textureUrls;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TextureUrl {
        @JsonProperty("base_color")
        private String baseColor;
    }
}
