package com.aicoding.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.aicoding.model.CodeTemplate;
import com.aicoding.model.Difficulty;

import lombok.Data;

@Data
public class ProblemResponse {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private List<CategoryResponse> categories;
    private List<TestCaseResponse> testCases;
    private List<CodeTemplate> codeTemplates;
    private UserSummaryResponse createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private int submissionCount;
    private double successRate;
}