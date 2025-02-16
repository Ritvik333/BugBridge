package com.example.demo.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Bug;
import com.example.demo.Service.BugService;
import com.example.demo.Service.FileStorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bugs")
public class BugController {
    @Autowired
    private BugService bugService;

    @Autowired
    private FileStorageService fileStorageService;

    // @GetMapping
    // public ResponseEntity<List<Bug>> getBugs() {
    //     return ResponseEntity.ok(bugService.getBugs(null, null, null, "createdAt", "desc"));
    // }

    @GetMapping
    public ResponseEntity<List<Bug>> getBugs(
        @RequestParam(required = false) String severity,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String creator,
        @RequestParam(defaultValue = "created_at") String sortBy,
        @RequestParam(defaultValue = "asc") String order
    ) {
        List<Bug> bugs = bugService.getBugs(severity, status, creator, sortBy, order);
        return ResponseEntity.ok(bugs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bug> getBugById(@PathVariable Long id) {
        Bug bug = bugService.getBugById(id);
        return bug != null ? ResponseEntity.ok(bug) : ResponseEntity.notFound().build();
    }

    // use form-data
    @PostMapping
    public ResponseEntity<Bug> createBug(
        @RequestParam String title,
        @RequestParam String severity,
        @RequestParam String status,
        @RequestParam String creator,
        // @RequestParam Integer priority,
        @RequestParam String description,
        @RequestParam String language,
        @RequestParam(value = "codeFilePath", required = false) MultipartFile codeFile
    ) throws IOException {
        Bug bug = new Bug();
        bug.setTitle(title);
        bug.setSeverity(severity);
        bug.setStatus(status);
        bug.setCreator(creator);
        bug.setLanguage(language);
        // bug.setPriority(priority);
        bug.setDescription(description);


        if (codeFile != null && !codeFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(codeFile);
            bug.setCodeFilePath(filePath); // Store file path instead of file content
        }

        return ResponseEntity.ok(bugService.createBug(bug));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bug> updateBug(
        @PathVariable Long id,
        @RequestParam String title,
        @RequestParam String severity,
        @RequestParam String status,
        @RequestParam String creator,
        @RequestParam String language,
        // @RequestParam Integer priority,
        @RequestParam String description,
        @RequestParam(value = "codeFilePath", required = false) MultipartFile codeFile
    ) throws IOException {
        Bug existingBug = bugService.getBugById(id);
        if (existingBug == null) {
            return ResponseEntity.notFound().build();
        }

        // Update fields
        existingBug.setTitle(title);
        existingBug.setSeverity(severity);
        existingBug.setStatus(status);
        existingBug.setCreator(creator);
        existingBug.setLanguage(language);
        // existingBug.setPriority(priority);
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
}
