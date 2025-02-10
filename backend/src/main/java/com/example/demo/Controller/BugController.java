package com.example.demo.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Bug;
import com.example.demo.Service.BugService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bugs")
public class BugController {
    @Autowired
    private BugService bugService;

    // @GetMapping
    // public ResponseEntity<List<Bug>> getBugs() {
    //     return ResponseEntity.ok(bugService.getBugs(null, null, null, "createdAt", "desc"));
    // }

    @GetMapping
public ResponseEntity<List<Bug>> getBugs(
    @RequestParam(required = false) String severity,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String creator,
    @RequestParam(defaultValue = "createdAt") String sortBy,
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

    // JSON testing 
    // @PostMapping
    // public ResponseEntity<Bug> createBug(
    //     @RequestBody Bug bug, // ✅ Accept JSON instead of form-data
    //     @RequestParam(value = "codeFile", required = false) MultipartFile codeFile
    // ) throws IOException {
    //     return ResponseEntity.ok(bugService.createBug(bug, codeFile));
    // }


    // use form-data
    @PostMapping
    public ResponseEntity<Bug> createBug(
        @RequestParam String title,
        @RequestParam String severity,
        @RequestParam String status,
        @RequestParam String creator,
        @RequestParam Integer priority,
        @RequestParam(value = "codeFile", required = false) MultipartFile codeFile
    ) throws IOException {
        Bug bug = new Bug();
        bug.setTitle(title);
        bug.setSeverity(severity);
        bug.setStatus(status);
        bug.setCreator(creator);
        bug.setPriority(priority);
        return ResponseEntity.ok(bugService.createBug(bug, codeFile));
    }
}
