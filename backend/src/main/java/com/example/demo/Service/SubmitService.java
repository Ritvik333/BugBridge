package com.example.demo.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Bug;
import com.example.demo.Model.Submit;
import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import com.example.demo.Repository.BugRepository;
import com.example.demo.Repository.SubmitRepository;

@Service
public class SubmitService {

    private final String storagePath = "uploads/";
    
    @Autowired
    private SubmitRepository submitRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BugRepository bugRepository;

    public Submit saveSubmission(Long userId, Long bugId, String username, String desc, String code) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Bug bug = bugRepository.findById(bugId)
            .orElseThrow(() -> new RuntimeException("Bug not found"));
        System.out.println(user);

        Submit submit = new Submit();
        submit.setUser(user);
        submit.setBug(bug);
        submit.setDescription(desc);
        if (bug.getCreator().getId().equals(userId)) {
            submit.setStatus("approved");
        } 
        Submit savedSubmit = submitRepository.save(submit);
        
        String extension = mapLanguageToExtension(bug.getLanguage());
        String filename = userId + "_" + bugId + "_" + savedSubmit.getId() + extension; // e.g., "302_4_10.java"
        Path directoryPath = Paths.get(storagePath, userId + "_" + username, "submissions");
        Path filePath = directoryPath.resolve(filename);
    
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        savedSubmit.setCodeFilePath(filePath.toString());
        
        // Submit existingSubmission = submitRepository.findByUserIdAndBugId(userId, bugId);
        
        // if (existingSubmission != null) {
        //     if (!bug.getCreator().getId().equals(userId)) {
        //         existingSubmission.setApprovalStatus("unapproved");
        //     } 
        //     existingSubmission.setCodeFilePath(filePath.toString());
        //     return submitRepository.save(existingSubmission);
        // } else {
            return submitRepository.save(savedSubmit);
        // }

    }

    public String mapLanguageToExtension(String language) {
        if (language == null) return ".txt";
        switch (language.toLowerCase()) {
            case "java": return ".java";
            case "python": return ".py";
            case "javascript": return ".js";
            default: return ".txt";
        }
    }

    public List<Submit> findApprovedSubmissionsByBugId(Long bugId) {
        List<Submit> approvedSubmissions = submitRepository.findByBugIdAndApprovalStatus(bugId, "approved");
        
        // Group by userId and get the most recent submission for each user
        Map<Long, Submit> latestPerUser = new HashMap<>();
        for (Submit submit : approvedSubmissions) {
            Long userId = submit.getUser().getId();
            Submit existing = latestPerUser.get(userId);
            if (existing == null || submit.getSubmittedAt().isAfter(existing.getSubmittedAt())) {
                latestPerUser.put(userId, submit);
            }
        }
        
        return new ArrayList<>(latestPerUser.values());
    }
      
    public List<Submit> getSubmissionsForUserAndBug(Long userId, Long bugId) {
        return submitRepository.findByUserIdAndBugId(userId, bugId);
    }
    public Submit getSubmissionById(Long submissionId) {
        return submitRepository.findById(submissionId)
                .orElse(null); // Return null if not found
    }
    
}
