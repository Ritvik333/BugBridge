package com.example.demo.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.io.IOException;

@Service
public class FileStorageService {
    private final String storagePath = "uploads/"; // Base storage path

    public String saveFile(MultipartFile file, Long userId, String username, String language) throws IOException {
        // Construct directory path: uploads/userId_username/language/
        Path directoryPath = Paths.get(storagePath, userId + "_" + username, language);

        // Ensure directories exist
        Files.createDirectories(directoryPath);

        // Keep the original filename
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IOException("Invalid file name");
        }

        Path filePath = directoryPath.resolve(filename);

        // Save file
        Files.write(filePath, file.getBytes());

        return filePath.toString(); // Return URL to access stored file
    }
}
