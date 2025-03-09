/*package com.example.testapi;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ExeController {

    private static final String UPLOAD_DIR = "C:/Users/user/Desktop/TTG/";
    private static final String SLICE_PROGRAM_PATH = "C:/Users/user/Desktop/macro3.exe";

    @PostMapping("/download-obj")
    public ResponseEntity<String> downloadObj(@RequestBody ObjUrlRequest request) {
        try {
            // OBJ 파일 다운로드
            RestTemplate restTemplate = new RestTemplate();
            byte[] objBytes = restTemplate.getForObject(request.getObjUrl(), byte[].class);
            String objFileName = Paths.get(new java.net.URL(request.getObjUrl()).getPath()).getFileName().toString();
            Path objFilePath = Paths.get(UPLOAD_DIR, objFileName);
            Files.write(objFilePath, objBytes);

            return ResponseEntity.ok("OBJ file downloaded successfully: " + objFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred while downloading OBJ file: " + e.getMessage());
        }
    }

    @GetMapping("/slice-and-download")
    public ResponseEntity<Resource> sliceAndDownload(@RequestParam String objFileName) {
        try {
            // 슬라이싱 프로그램 실행
            ProcessBuilder processBuilder = new ProcessBuilder(SLICE_PROGRAM_PATH, UPLOAD_DIR + objFileName);
            processBuilder.directory(new File("C:/Users/user/Desktop"));
            Process process = processBuilder.start();
            process.waitFor();

            // 슬라이싱된 파일의 이름과 경로 설정
            String slicedFileName = objFileName.replace(".obj", "_sliced.obj"); // 슬라이싱된 파일의 이름 규칙에 따라 변경
            Path slicedFilePath = Paths.get(UPLOAD_DIR, slicedFileName);

            if (!Files.exists(slicedFilePath)) {
                return ResponseEntity.status(500).body(null);
            }

            // 슬라이싱된 파일을 리소스로 로드
            Resource resource = new FileSystemResource(slicedFilePath.toFile());

            // 파일 다운로드를 위한 HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}

class ObjUrlRequest {
    private String objUrl;

    public String getObjUrl() {
        return objUrl;
    }

    public void setObjUrl(String objUrl) {
        this.objUrl = objUrl;
    }
}
*/