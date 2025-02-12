package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.RunService;

import lombok.Data;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Adjust as per frontend URL
public class RunController {

    @Autowired
    private RunService runService;

    @PostMapping("/run")
    public OutputResponse runCode(@RequestBody CodeRequest request) {
        String code = request.getCode();
        String language = request.getLanguage();

        if (code == null || code.isEmpty() || language == null || language.isEmpty()) {
            return new OutputResponse("Error: Code or language not provided");
        }

        try {
            String output = runService.executeCode(code, language);
            return new OutputResponse(output);
        } catch (Exception e) {
            return new OutputResponse("Error executing code: " + e.getMessage());
        }
    }

    @Data
    static class CodeRequest {
        private String code;
        private String language;
    }

    @Data
    static class OutputResponse {
        private final String output;
    }
}
