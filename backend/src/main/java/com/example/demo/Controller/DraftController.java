package com.example.demo.Controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Draft;
import com.example.demo.Service.DraftService;
import com.example.demo.dto.DraftRequestDto;

@RestController
@RequestMapping("/drafts")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

   @PostMapping("/save")
public Draft saveDraft(@RequestBody DraftRequestDto request) throws IOException {
    return draftService.saveDraftFile(request.getUserId(), request.getBugId(), request.getCode());
}

    // @GetMapping("/user/{userId}")
    // public List<Draft> getUserDrafts(@PathVariable Long userId) {
    //     return draftService.getDraftsForUser(new User(userId));
    // }

    // @GetMapping("/bug/{userId}/{bugId}")
    // public List<Draft> getBugDrafts(@PathVariable Long userId, @PathVariable Long bugId) {
    //     return draftService.getDraftsForBug(new User(userId), new Bug(bugId));
    // }
}
