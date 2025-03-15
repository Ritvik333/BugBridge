
package com.example.demo.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Model.Bug;
import com.example.demo.dto.SuggestedSolution;

@Service
public class BugSuggestionService {

    public List<SuggestedSolution> getSuggestedSolutions(Bug bug) {
        return List.of();
    }



}
