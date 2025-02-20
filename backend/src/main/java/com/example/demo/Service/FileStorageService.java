package com.example.demo.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private final String storagePath = "uploads/"; // Local storage path (can be replaced with S3, GCS, etc.)

    public String saveFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(storagePath, filename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        return filePath.toString(); // Return URL to access stored file
    }
}
