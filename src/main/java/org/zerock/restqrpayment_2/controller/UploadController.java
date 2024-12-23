package org.zerock.restqrpayment_2.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Log4j2
public class UploadController {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    private final Map<String, String> contentTypeMap = new HashMap<String, String>() {{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("webp", "image/webp");
        put("gif", "image/gif");
    }};

    @PostMapping("/api/owner/upload")
    public ResponseEntity<List<String>> upload(@RequestParam("files") MultipartFile[] files) {
        List<String> uploadedFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalName = file.getOriginalFilename();
                String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
                
                // 파일 확장자 확인
                String extension = getFileExtension(fileName).toLowerCase();
                if (!contentTypeMap.containsKey(extension)) {
                    log.warn("Unsupported file type: {}", extension);
                    continue;
                }

                String uuid = UUID.randomUUID().toString();
                String saveName = uuid + "_" + fileName;
                Path savePath = Paths.get(uploadPath, saveName);

                try {
                    Files.createDirectories(savePath.getParent());
                    file.transferTo(savePath);
                    uploadedFileNames.add(saveName);

                    // 썸네일 생성 (webp 파일도 지원)
                    if (contentTypeMap.containsKey(extension)) {
                        Path thumbnailPath = Paths.get(uploadPath, "s_" + saveName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbnailPath.toFile(), 100, 100);
                    }
                } catch (IOException e) {
                    log.error("Error saving file: {} - {}", fileName, e.getMessage(), e);
                    continue;
                }
            }
        }

        return ResponseEntity.ok(uploadedFileNames);
    }

    @GetMapping("/api/owner/display")
    public ResponseEntity<byte[]> display(@RequestParam String fileName) {
        try {
            String filePath = uploadPath + File.separator + fileName;
            File file = new File(filePath);
            
            if (!file.exists()) {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            String contentType;
            if (fileName.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else {
                contentType = "application/octet-stream";
            }

            byte[] fileData = Files.readAllBytes(file.toPath());
            
            // 디버깅 로그 추가
            log.info("File found: {}", filePath);
            log.info("File size: {} bytes", fileData.length);
            log.info("Content type: {}", contentType);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileData);
                    
        } catch (IOException e) {
            log.error("Error displaying file: {} - {}", fileName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    @DeleteMapping("/api/owner/removeFile")
    public ResponseEntity<Boolean> removeFile(@RequestParam String fileName) {
        try {
            Path filePath = Paths.get(uploadPath, fileName);
            Path thumbnailPath = Paths.get(uploadPath, "s_" + fileName);
            
            boolean result = Files.deleteIfExists(filePath);
            Files.deleteIfExists(thumbnailPath);
            
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Error removing file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
