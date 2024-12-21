package org.zerock.restqrpayment_2.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    @Value("${file.upload.path:/Users/krrrrrng/Desktop/qr/qr1216/uploads}")
    private String uploadPath;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 업로드 디렉토리 생성
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path savePath = Paths.get(uploadPath, savedFileName);
        Files.copy(file.getInputStream(), savePath);

        log.info("File saved: " + savedFileName);
        return savedFileName;
    }

    public void deleteFile(String fileName) {
        if (fileName == null) {
            return;
        }

        Path filePath = Paths.get(uploadPath, fileName);
        try {
            Files.deleteIfExists(filePath);
            log.info("File deleted: " + fileName);
        } catch (IOException e) {
            log.error("Error deleting file: " + fileName, e);
        }
    }

    public Path getFilePath(String fileName) {
        return Paths.get(uploadPath, fileName);
    }
}
