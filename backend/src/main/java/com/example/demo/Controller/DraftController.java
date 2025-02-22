package com.example.demo.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Draft;
import com.example.demo.Service.DraftService;
import com.example.demo.dto.DraftRequestDto;
import com.example.demo.dto.ResponseWrapper;

@RestController
@RequestMapping("/drafts")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @PostMapping("/save")
    public ResponseWrapper<Draft> saveDraft(@RequestBody DraftRequestDto request) throws IOException {
        try {
            Draft savedDraft = draftService.saveDraftFile(request.getUserId(), request.getBugId(), request.getCode());
            return new ResponseWrapper<>("success", "Draft saved successfully", savedDraft);
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
}
