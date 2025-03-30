package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.RunService;
import com.example.demo.Service.RunServiceFactory;
import com.example.demo.dto.ResponseWrapper;

import lombok.Data;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Adjust as per frontend URL
public class RunController {

    @Qualifier("javaRunService")
    @Autowired
    private RunService runService;

    @Autowired
    private RunServiceFactory runServiceFactory;

        @PostMapping("/run")
        public ResponseWrapper<OutputResponse> runCode(@RequestBody CodeRequest request) {
            String code = request.getCode();
            String language = request.getLanguage();

            // Validate input and return early if invalid
            ResponseWrapper<OutputResponse> validationResult = validateInput(code, language);
            if (validationResult != null) {
                return validationResult;
            }

            try {
                RunService runner = runServiceFactory.getRunService(language);
                String output = runner.executeCode(code);
                OutputResponse successResponse = new OutputResponse(output);
                return new ResponseWrapper<>("success", "Code executed successfully", successResponse);
            } catch (Exception e) {
                OutputResponse errorResponse = new OutputResponse("Error executing code: " + e.getMessage());
                return new ResponseWrapper<>("error", "Execution failed", errorResponse);
            }
        }

    // Validation method to check code and language
    private ResponseWrapper<OutputResponse> validateInput(String code, String language) {
        if (code == null || code.isEmpty()) {
            return createErrorResponse("Code not provided");
        }
        if (language == null || language.isEmpty()) {
            return createErrorResponse("Language not provided");
        }
        return null; // Indicates valid input
    }

    // Helper method to create error responses
    private ResponseWrapper<OutputResponse> createErrorResponse(String message) {
        OutputResponse errorResponse = new OutputResponse("Error: " + message);
        return new ResponseWrapper<>("error", message, errorResponse);
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