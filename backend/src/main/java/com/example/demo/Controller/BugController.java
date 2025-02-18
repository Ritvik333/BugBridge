package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Bug;
import com.example.demo.Model.User;
import com.example.demo.Service.BugService;
import com.example.demo.Service.FileStorageService;
import com.example.demo.Service.UserService;

@RestController

@RequestMapping("/api/bugs")
public class BugController {
    @Autowired
    private BugService bugService;

    @Autowired
    private UserService userService; // Fetch User entity

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<Bug>> getBugs(
        @RequestParam(required = false) String severity,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Long creatorId, // Now Long instead of String
        @RequestParam(defaultValue = "created_at") String sortBy,
        @RequestParam(defaultValue = "asc") String order
    ) {
        List<Bug> bugs = bugService.getBugs(severity, status, creatorId, sortBy, order); // Fixed method call
        return ResponseEntity.ok(bugs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bug> getBugById(@PathVariable Long id) {
        Bug bug = bugService.getBugById(id);
        return bug != null ? ResponseEntity.ok(bug) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createBug(
        @RequestParam String title,
        @RequestParam String severity,
        @RequestParam String status,
        @RequestParam Long creatorId, // Now Long instead of String
        @RequestParam String description,
        @RequestParam String language,
        @RequestParam(value = "codeFilePath", required = false) MultipartFile codeFile
    ) throws IOException {
        User creator = userService.getUserById(creatorId); // Fetch User entity
        if (creator == null) {
            return ResponseEntity.badRequest().body("Invalid creator ID");
        }

        Bug bug = new Bug();
        bug.setTitle(title);
        bug.setSeverity(severity);
        bug.setStatus(status);
        bug.setCreator(creator); // Now correctly passing a User object
        bug.setLanguage(language);
        bug.setDescription(description);

        if (codeFile != null && !codeFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(codeFile);
            bug.setCodeFilePath(filePath);
        }

        return ResponseEntity.ok(bugService.createBug(bug));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBug(
        @PathVariable Long id,
        @RequestParam String title,
        @RequestParam String severity,
        @RequestParam String status,
        @RequestParam Long creatorId, // Now Long instead of String
        @RequestParam String language,
        @RequestParam String description,
        @RequestParam(value = "codeFilePath", required = false) MultipartFile codeFile
    ) throws IOException {
        Bug existingBug = bugService.getBugById(id);
        if (existingBug == null) {
            return ResponseEntity.notFound().build();
        }

        User creator = userService.getUserById(creatorId); // Fetch User entity
        if (creator == null) {
            return ResponseEntity.badRequest().body("Invalid creator ID");
        }

        existingBug.setTitle(title);
        existingBug.setSeverity(severity);
        existingBug.setStatus(status);
        existingBug.setCreator(creator); // Now correctly passing a User object
        existingBug.setLanguage(language);
        existingBug.setDescription(description);

        if (codeFile != null && !codeFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(codeFile);
            existingBug.setCodeFilePath(filePath);
        }

        return ResponseEntity.ok(bugService.updateBug(existingBug));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBug(@PathVariable Long id) {
        boolean deleted = bugService.deleteBug(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/file/{filename}")
    public ResponseEntity<String> getFileContent(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads", filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        String content = Files.readString(filePath);
        return ResponseEntity.ok(content);
    }

}
