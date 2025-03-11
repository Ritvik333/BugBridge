package com.example.demo.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private NotificationService notificationService;


    public Submit saveSubmission(Long userId, Long bugId, String username, String desc, String code) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Bug bug = bugRepository.findById(bugId)
            .orElseThrow(() -> new RuntimeException("Bug not found"));
        System.out.println(user);
        Path directoryPath = Paths.get(storagePath, userId + "_" + username, "submissions");
        String extension = mapLanguageToExtension(bug.getLanguage());
        String filename = userId + "_" + bugId + extension;
        Path filePath = directoryPath.resolve(filename);
    
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    
        // Submit existingSubmission = submitRepository.findByUserIdAndBugId(userId, bugId);
        
        // if (existingSubmission != null) {
        //     existingSubmission.setCodeFilePath(filePath.toString());
        //     return submitRepository.save(existingSubmission);
        // } else {
        //     Submit submit = new Submit();
        //     submit.setUser(user);
        //     submit.setBug(bug);
        //     submit.setDescription(desc);
        //     submit.setCodeFilePath(filePath.toString());
        //     return submitRepository.save(submit);
        // }
        Submit submit = new Submit();
            submit.setUser(user);
            submit.setBug(bug);
            submit.setDescription(desc);
            submit.setCodeFilePath(filePath.toString());
            Submit savedSubmit= submitRepository.save(submit);

             // Create notification
            notificationService.createNotification( userId, "Your submission for bug #" + bug.getId() + " has been submitted.");

            // Notify the bug creator
            Long bugCreatorId = bug.getCreator().getId();
            notificationService.createNotification(bugCreatorId, 
                "A new submission has been made for your bug #" + bug.getId() + " by " + user.getUsername() + ".");

        return savedSubmit;

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
    
    public List<Submit> getSubmissionsForUserAndBug(Long userId, Long bugId) {
        return submitRepository.findByUserIdAndBugId(userId, bugId);
    }

    public String approveSubmission(Long submissionId, Long approverId) {
        if (submissionId == null || approverId == null) {
            throw new NullPointerException("Submission ID and Approver ID cannot be null");
        }

        Optional<Submit> submissionOptional = submitRepository.findById(submissionId);
        
        if (submissionOptional.isEmpty()) {
            return "Submission not found.";
        }

        Submit submission = submissionOptional.get();
        Bug bug = submission.getBug();

        if (!bug.getCreator().getId().equals(approverId)) {
            return "Only the bug creator can approve submissions.";
        }        

        submission.setApprovalStatus("approved");
        submitRepository.save(submission);

        notificationService.createNotification(submission.getUser().getId(), 
            "Your submission for bug #" + bug.getId() + " has been approved.");

        return "Submission approved successfully.";
    }

    public List<Submit> getUnapprovedSubmissions() {
        return submitRepository.findByApprovalStatus("unapproved");
    }

    public List<Submit> getApprovedSubmissions() {
        return submitRepository.findByApprovalStatus("approved");
    }
}

