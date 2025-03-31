//package com.example.demo.Service;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//public class FileStorageService {
//    private final String storagePath = "uploads/"; // Base storage path
//
//    public String saveFile(MultipartFile file, Long userId, String username, String language) throws IOException {
//        // Construct directory path: uploads/userIdusername/language/
//        Path directoryPath = Paths.get(storagePath, userId + "_" + username, language);
//
//        // Ensure directories exist
//        Files.createDirectories(directoryPath);
//
//        // Keep the original filename
//        String filename = file.getOriginalFilename();
//        if (filename == null || filename.isBlank()) {
//            throw new IOException("Invalid file name");
//        }
//
//        Path filePath = directoryPath.resolve(filename);
//
//        // Save file
//        Files.write(filePath, file.getBytes());
//
//        return filePath.toString(); // Return URL to access stored file
//    }
//}
package com.example.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private String storagePath; // Removed final so it can be updated via constructor

    // Default constructor
    public FileStorageService() {
        this.storagePath = "uploads/";
    }

    // New constructor that accepts a custom storage path
    public FileStorageService(String storagePath) {
        this.storagePath = storagePath;
    }

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

        return filePath.toString(); // Return path to access stored file
    }
}
