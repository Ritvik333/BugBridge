package com.example.demo.Model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "bugs")
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String severity;
    private String status;
    private String creator;
    // private Integer priority;   
    private String description;     
    @Column(name = "code_file_path") // Store file path
    private String codeFilePath;
    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String language;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public String getCodeFilePath() { 
        return codeFilePath; 
    }

    public void setCodeFilePath(String codeFilePath) { 
        this.codeFilePath = codeFilePath; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    // public Integer getPriority() {
    //     return priority;
    // }

    // public void setPriority(Integer priority) {
    //     this.priority = priority;
    // }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // public String getCodeUrl() {
    //     return codeUrl;
    // }

    // public void setCodeUrl(String codeUrl) {
    //     this.codeUrl = codeUrl;
    // }
}
