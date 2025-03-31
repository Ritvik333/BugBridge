package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Model.Comment;
import com.example.demo.Model.User;
import com.example.demo.Service.CommentService;
import com.example.demo.Service.UserService;
import com.example.demo.dto.ResponseWrapper;

import lombok.Data;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByBugId(@RequestParam Long bugId) {
        List<Comment> comments = commentService.getCommentsByBugId(bugId);
        return ResponseEntity.ok(comments);
    }

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

    @DeleteMapping("/{commentId}")
    public ResponseWrapper<Void> deleteComment(@PathVariable Long commentId) {
        try {
            boolean deleted = commentService.deleteCommentById(commentId);
            if (deleted) {
                return new ResponseWrapper<>("success", "Comment deleted successfully", null);
            } else {
                return new ResponseWrapper<>("error", "Comment not found", null);
            }
        } catch (Exception e) {
            return new ResponseWrapper<>("error", "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

    @Data
    public static class CommentRequest {
        private Long bugId;
        private Long userId;
        private String text;
    }
}
