package com.example.demo.Controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Draft;
import com.example.demo.Model.User;
import com.example.demo.Service.DraftService;
import com.example.demo.Service.UserService;
import com.example.demo.dto.DraftRequestDto;
import com.example.demo.dto.ResponseWrapper;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/drafts")
public class DraftController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private DraftService draftService;

    @PostMapping("/save")
    public ResponseWrapper<Draft> saveDraft(@RequestBody DraftRequestDto request) throws IOException {
        try {
            // Retrieve the user by ID
            User user = userService.getUserById(request.getUserId());

            // Extract the parameters to pass to the service
            Long userId = request.getUserId();
            Long bugId = request.getBugId();
            String username = user.getUsername();
            String code = request.getCode();

            // Pass parameters to the service method
            Draft savedDraft = draftService.saveDraftFile(userId, bugId, username, code);

            return new ResponseWrapper<>("success", "Draft saved successfully", savedDraft);
        } catch (EntityNotFoundException e) {
            return new ResponseWrapper<>("error", "User not found", null);
        } catch (IOException e) {
            return new ResponseWrapper<>("error", "Failed to save draft", null);
        }
    }



    @GetMapping("/user/{userId}")
    public ResponseWrapper<List<Draft>> getUserDrafts(@PathVariable Long userId) {
        try {
            List<Draft> drafts = draftService.getDraftsForUser(userId);
            return new ResponseWrapper<>("success", "Fetched drafts successfully", drafts);
        } catch (Exception e) {
            return new ResponseWrapper<>("error", "Failed to fetch drafts", null);
        }
    }

    @GetMapping("/file/{userId}/{username}/{language}/{filename}")
    public ResponseEntity<String> getFileContent(
            @PathVariable Long userId,
            @PathVariable String username,
            @PathVariable String language,
            @PathVariable String filename) throws IOException {

        // Construct individual path components
        String userFolder = userId + "_" + username;
        String draftsFolder = "drafts";
        String fileExtension = getExtension(filename);
        String fileName = userId + "_" + language + "." + fileExtension;

        // Construct the full path
        Path filePath = Paths.get("uploads", userFolder, draftsFolder, fileName);
        System.out.println(filePath);

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        return ResponseEntity.ok(content);
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf('.');
        // Check if a dot exists and isn't the last character
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";  // Return empty string if no extension found
        }
        return filename.substring(dotIndex + 1);
    }

}
