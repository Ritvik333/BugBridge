package com.example.demo.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Repository.BugRepository;
import com.example.demo.Model.Bug;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class BugService {
    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private FileStorageService fileStorageService;

    public List<Bug> getBugs(String severity, String status, String creator, String sortBy, String order) {
        return bugRepository.findAll();
    }

    public Bug getBugById(Long id) {
        return bugRepository.findById(id).orElse(null);
    }

    public Bug createBug(Bug bug, MultipartFile codeFile) throws IOException {
        if (codeFile != null && !codeFile.isEmpty()) {
            // Convert file to String and store it
            String fileContent = new String(codeFile.getBytes(), StandardCharsets.UTF_8);
            bug.setCode(fileContent);
        }
        return bugRepository.save(bug);
    }
    
}
