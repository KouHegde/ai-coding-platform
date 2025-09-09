package com.aicoding.dto.request;

import com.aicoding.model.ProgrammingLanguage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for code template creation and update requests
 */
public class CodeTemplateRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;
    
    @NotNull(message = "Programming language is required")
    private ProgrammingLanguage language;
    
    @NotBlank(message = "Template code cannot be blank")
    @Size(max = 10000, message = "Template code cannot exceed 10000 characters")
    private String templateCode;
    
    @Size(max = 1000, message = "Function signature cannot exceed 1000 characters")
    private String functionSignature;
    
    @Size(max = 5000, message = "Comments cannot exceed 5000 characters")
    private String comments;

    // Getters and Setters
    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getFunctionSignature() {
        return functionSignature;
    }

    public void setFunctionSignature(String functionSignature) {
        this.functionSignature = functionSignature;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}