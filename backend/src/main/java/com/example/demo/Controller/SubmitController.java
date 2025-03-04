package com.example.demo.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Submit;
import com.example.demo.Model.User;
import com.example.demo.Service.SubmitService;
import com.example.demo.Service.UserService;
import com.example.demo.dto.ResponseWrapper;
import com.example.demo.dto.SubmitRequestDto;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/submissions")
public class SubmitController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SubmitService submitService;

    @PostMapping("/save")
    public ResponseWrapper<Submit> saveSubmission(@RequestBody SubmitRequestDto request) {
        try {
            User user = userService.getUserById(request.getUserId());
            
            // Pass username along with other parameters to saveSubmission
            Submit savedSubmit = submitService.saveSubmission(request.getUserId(), request.getBugId(), user.getUsername(), request.getDesc(), request.getCode());
            
            return new ResponseWrapper<>("success", "Submission saved successfully", savedSubmit);
        } catch (EntityNotFoundException e) {
            return new ResponseWrapper<>("error", "User not found", null);
        } catch (IOException e) {
            return new ResponseWrapper<>("error", "Failed to save submission", null);
        }
    }

    @GetMapping("/user/{userId}/bug/{bugId}")
    public ResponseWrapper<List<Submit>> getUserSubmissionsByBug(@PathVariable Long userId, @PathVariable Long bugId) {
        try {
            List<Submit> submissions = submitService.getSubmissionsForUserAndBug(userId, bugId);
            return new ResponseWrapper<>("success", "Fetched submissions successfully", submissions);
        } catch (Exception e) {
            return new ResponseWrapper<>("error", "Failed to fetch submissions", null);
        }
    }
}
