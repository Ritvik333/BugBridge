package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.example.demo.Model.Comment;
import com.example.demo.Model.User;
import com.example.demo.Service.CommentService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.Model.Bug;
import com.example.demo.Service.BugService;
import com.example.demo.Service.FileStorageService;
import com.example.demo.Service.UserService;
import com.example.demo.dto.ResponseWrapper;
import java.time.LocalDateTime;


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
public ResponseWrapper<Bug> createBug(
    @RequestParam String title,
    @RequestParam String severity,
    @RequestParam String status,
    @RequestParam Long creatorId,
    @RequestParam String description,
    @RequestParam String language,
    @RequestParam(value = "codeFilePath", required = false) MultipartFile codeFile
) throws IOException {
    try {
        User creator = userService.getUserById(creatorId);
        if (creator == null) {
            return new ResponseWrapper<>("error", "Invalid creator ID", null);
        }

        Bug bug = new Bug();
        bug.setTitle(title);
        bug.setSeverity(severity);
        bug.setStatus(status);
        bug.setCreator(creator);
        bug.setLanguage(language);
        bug.setDescription(description);

        if (codeFile != null && !codeFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(codeFile);
            bug.setCodeFilePath(filePath);
        }

        Bug createdBug = bugService.createBug(bug);
        return new ResponseWrapper<>("success", "Bug created successfully", createdBug);

    } catch (Exception e) {
        return new ResponseWrapper<>("error", "An unexpected error occurred", null);
    }
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

    @RestController
    @RequestMapping("/api/comments")
    public class CommentController {

        @Autowired
        private CommentService commentService;

        @Autowired
        private UserService userService;

        // GET endpoint to retrieve comments for a given bugId
        @GetMapping
        public ResponseEntity<List<Comment>> getCommentsByBugId(@RequestParam Long bugId) {
            List<Comment> comments = commentService.getCommentsByBugId(bugId);
            return ResponseEntity.ok(comments);
        }

        // POST endpoint to create a new comment using a request DTO
        @PostMapping
        public ResponseWrapper<Comment> createComment(@RequestBody CommentRequest request) {
            if (request.getBugId() == null || request.getUserId() == null || request.getText() == null || request.getText().isEmpty()) {
                return new ResponseWrapper<>("error", "Bug ID, User ID, and text are required", null);
            }
            try {
                User user = userService.getUserById(request.getUserId());
                if (user == null) {
                    return new ResponseWrapper<>("error", "Invalid user ID", null);
                }
                Comment comment = new Comment();
                comment.setBugId(request.getBugId());
                comment.setUser(user);
                comment.setText(request.getText());
                comment.setTimestamp(LocalDateTime.now());

                Comment createdComment = commentService.createComment(comment);
                return new ResponseWrapper<>("success", "Comment created successfully", createdComment);
            } catch (Exception e) {
                return new ResponseWrapper<>("error", "An unexpected error occurred: " + e.getMessage(), null);
            }
        }

        @Data
        static class CommentRequest {
            private Long bugId;
            private Long userId;
            private String text;

            public Long getBugId() {
                return bugId;
            }

            public void setBugId(Long bugId) {
                this.bugId = bugId;
            }

            public Long getUserId() {
                return userId;
            }

            public void setUserId(Long userId) {
                this.userId = userId;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

    }

}
