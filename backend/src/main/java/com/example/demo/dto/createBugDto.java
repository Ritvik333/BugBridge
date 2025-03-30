package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class createBugDto {
    private String title;
    private String severity;
    private String status;
    private Long creatorId;
    private String description;
    private String language;
    private MultipartFile codeFile;

    // Getters
    public String getTitle() {
        return title;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public MultipartFile getCodeFile() {
        return codeFile;
    }
}
