package com.example.demo.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Bug;
import com.example.demo.Model.Draft;
import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import com.example.demo.Repository.BugRepository;
import com.example.demo.Repository.DraftRepository;

@Service
public class DraftService {

    private final String storagePath = "uploads/drafts/";
    @Autowired
    private DraftRepository draftRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BugRepository bugRepository;


    public Draft saveDraftFile(Long userId, Long bugId, String code) throws IOException {
        // Retrieve user and bug
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Bug bug = bugRepository.findById(bugId)
            .orElseThrow(() -> new RuntimeException("Bug not found"));
    
        // Map language to file extension
        String extension = mapLanguageToExtension(bug.getLanguage());
    
        // Generate filename
        String filename = userId + "_" + bugId + extension;
        Path filePath = Paths.get(storagePath, filename);
    
        // Ensure directory exists
        Files.createDirectories(filePath.getParent());
    
        // Write code to file
        Files.write(filePath, code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    
        // Create and save Draft entity
        Draft draft = new Draft();
        draft.setUser(user);
        draft.setBug(bug);
        draft.setCodeFilePath(filePath.toString());
    
        return draftRepository.save(draft);
    }
    
    // Utility method for mapping languages to file extensions
    private String mapLanguageToExtension(String language) {
        if (language == null) return ".txt"; // Default extension
    
        switch (language.toLowerCase()) {
            case "java": return ".java";
            case "python": return ".py";
            case "javascript": return ".js";
            default: return ".txt"; // Default if unknown
        }
    }
    

    public List<Draft> getDraftsForUser(User user) {
        return draftRepository.findByUser(user);
    }

    public List<Draft> getDraftsForBug(User user, Bug bug) {
        return draftRepository.findByUserAndBug(user, bug);
    }
}
