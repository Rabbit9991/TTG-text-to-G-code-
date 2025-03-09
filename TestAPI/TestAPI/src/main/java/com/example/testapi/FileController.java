package com.example.testapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {

    private static final String UPLOAD_DIR = "C:/Users/user/Desktop/TTG/";
    private static final String UPLOAD_IMG = "C:/Users/user/Desktop/TTG_image/";
    private static final String SLICE_PROGRAM_PATH = "C:/Users/user/Desktop/macro4.exe";
    private static final int MAX_WAIT_TIME = 60000; // 최대 대기 시간 60초 (60000밀리초)
    private static final int WAIT_INTERVAL = 1000; // 대기 간격 1초 (1000밀리초)

    private final QueueManager queueManager = new QueueManager();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ImageService imageService;

    @GetMapping("/download-obj")
    public ResponseEntity<Map<String, String>> downloadObj(@RequestParam String objUrl) {
        try {
            String fileName = generateFileName(objUrl) + ".obj";
            Path path = Paths.get(UPLOAD_DIR, fileName);
            downloadFile(restTemplate, objUrl, path);

            Map<String, String> response = new HashMap<>();
            response.put("message", "OBJ file downloaded successfully to " + path.toString());
            response.put("fileName", fileName);
            return ResponseEntity.ok(response);
        } catch (IOException | URISyntaxException e) {
            //e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "File download failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download-img")
    public ResponseEntity<Resource> downloadimgFile(@RequestParam String imgUrl, @RequestParam String objFileName, @RequestParam String prompt) {
        String imgname = objFileName+ ".png";
        try {
            Path img_filePath = Paths.get(UPLOAD_IMG, imgname);
            downloadFile(restTemplate, imgUrl, img_filePath);

            imageService.saveImage(imgname, prompt); // 이미지 이름과 프롬프트를 저장

            Map<String, String> response1 = new HashMap<>();
            response1.put("message", "OBJ file downloaded successfully to " + img_filePath.toString());
            response1.put("fileName", imgname);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }


        return null;
    }

    @GetMapping("/slice-and-download")
    public ResponseEntity<Map<String, Object>> sliceAndDownload(@RequestParam String objFileName) {
        String userId = objFileName.replace(".obj", ""); // 파일 이름에서 .obj를 제거하여 userId 생성
        try {
            queueManager.enterQueue(userId);

            // 대기 상태 체크 및 위치 반환
            int position = queueManager.getPosition(userId);
            while (position != 0) {
                Thread.sleep(WAIT_INTERVAL);
                position = queueManager.getPosition(userId);
            }

            // 슬라이싱 프로그램 실행
            ProcessBuilder processBuilder = new ProcessBuilder(SLICE_PROGRAM_PATH, objFileName);
            processBuilder.directory(new File("C:/Users/user/Desktop"));
            Process process = processBuilder.start();
            process.waitFor();

            // .hvs 파일이 생성될 때까지 대기
            Path hvsFilePath = Paths.get(UPLOAD_DIR, userId + ".hvs");
            long startTime = System.currentTimeMillis();
            while (!Files.exists(hvsFilePath)) {
                if (System.currentTimeMillis() - startTime > MAX_WAIT_TIME) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "HVS file creation timed out.");
                    return ResponseEntity.status(500).body(response);
                }
                Thread.sleep(WAIT_INTERVAL);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "HVS file created successfully.");
            response.put("hvsFileName", hvsFilePath.getFileName().toString());
            response.put("userId", userId);
            response.put("position", queueManager.getPosition(userId)); // position 값을 Map에 추가
            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } finally {
            queueManager.leaveQueue(userId);
        }
    }

    @GetMapping("/queue-status")
    public ResponseEntity<Map<String, Object>> getQueueStatus(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        if (userId != null && !userId.isEmpty()) {
            int position = queueManager.getPosition(userId);
            response.put("position", position);
        } else {
            response.put("position", -1); // userId가 없는 경우 기본값 설정
        }
        response.put("queueSize", queueManager.getQueueSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download-hvs")
    public ResponseEntity<Resource> downloadHvsFile(@RequestParam String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Resource resource = new FileSystemResource(filePath.toFile());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    private String generateFileName(String url) throws NoSuchAlgorithmException {
        // URL 해시 생성
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(url.getBytes());
        String urlHash = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

        // 타임스탬프 추가
        long timestamp = System.currentTimeMillis() / 1000; // 초 단위 타임스탬프

        return urlHash + "_" + timestamp;
    }

    private void downloadFile(RestTemplate restTemplate, String url, Path path) throws IOException, URISyntaxException {
        byte[] fileBytes = restTemplate.getForObject(new URI(url), byte[].class);
        Files.write(path, fileBytes);
        System.out.println("File downloaded successfully to " + path.toString());
    }
}
