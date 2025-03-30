package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class getBugsDto { // Class name should start with uppercase
    private String severity;
    private String status;
    private Long creatorId;
    private String sortBy = "created_at"; // Default value
    private String order = "asc";         // Default value

    // Getters
    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getOrder() {
        return order;
    }
}
