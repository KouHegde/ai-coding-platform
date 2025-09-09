package com.aicoding.dto.response;

import lombok.Data;

@Data
public class TestResultResponse {
    private Long id;
    private Long testCaseId;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private boolean passed;
    private String errorMessage;
    private Long executionTimeMs;
    private Long memoryUsageBytes;
    private Double similarityScore;
}