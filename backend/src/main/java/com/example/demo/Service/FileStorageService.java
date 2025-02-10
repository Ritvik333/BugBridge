package com.example.demo.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {
    private final String storagePath = "uploads/"; // Local storage path (can be replaced with S3, GCS, etc.)

    public String saveFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(storagePath, filename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        return "/files/" + filename; // Return URL to access stored file
    }
}
