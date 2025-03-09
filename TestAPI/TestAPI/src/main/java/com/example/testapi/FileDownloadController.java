/*
        package com.example.testapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class FileDownloadController {

    private static final String UPLOAD_DIR = "C:/Users/user/Desktop/TTG/";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/download-obj")
    public ResponseEntity<String> downloadObj(@RequestParam String objUrl) {
        try {
            String fileName = generateFileName(objUrl);
            Path path = Paths.get(UPLOAD_DIR, fileName + ".obj");
            downloadFile(restTemplate, objUrl, path);
            return ResponseEntity.ok("OBJ file downloaded successfully to " + path.toString());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File download failed: " + e.getMessage());
        }
    }

    private String generateFileName(String url) {
        // URL 중간에서 6자리 추출
        int urlLength = url.length();
        int startIndex = urlLength / 2 - 3; // 중간에서 3자리 앞
        int endIndex = startIndex + 6; // 6자리 추출
        String middlePart = url.substring(startIndex, Math.min(endIndex, urlLength)).replaceAll("[^a-zA-Z0-9]", "_");

        // UUID 생성
        String uniqueID = UUID.randomUUID().toString();

        return middlePart + "_" + uniqueID;
    }

    private void downloadFile(RestTemplate restTemplate, String url, Path path) throws IOException, URISyntaxException {
        byte[] fileBytes = restTemplate.getForObject(new URI(url), byte[].class);
        Files.write(path, fileBytes);
        System.out.println("File downloaded successfully to " + path.toString());
    }
}*/
