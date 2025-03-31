package com.example.demo.Service;
import java.util.List;
import java.util.Optional;

import com.example.demo.dto.getBugsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Bug;
import com.example.demo.Repository.BugRepository;

@Service
public class    BugService {
    @Autowired
    private BugRepository bugRepository;

    public List<Bug> getBugs(getBugsDto filter) {
        return bugRepository.findAll(); // Modify this to apply filtering based on filter fields
    }

    public Bug getBugById(Long id) {
        return bugRepository.findById(id).orElse(null);
    }

    public Bug createBug(Bug bug) {
        return bugRepository.save(bug);
    }
    
    public Bug updateBug(Bug updatedBug) {
        Optional<Bug> existingBugOptional = bugRepository.findById(updatedBug.getId());
        if (existingBugOptional.isPresent()) {
            return bugRepository.save(updatedBug);
        }
        return null; // Return null if the bug doesn't exist
    }

    public boolean deleteBug(Long id) {
        Optional<Bug> existingBugOptional = bugRepository.findById(id);
        if (existingBugOptional.isPresent()) {
            bugRepository.deleteById(id);
            return true; // Successfully deleted
        }
        return false; // Bug not found
    }
}
