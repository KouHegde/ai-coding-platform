package com.aicoding.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.aicoding.model.ProgrammingLanguage;
import com.aicoding.model.SubmissionStatus;

import lombok.Data;

@Data
public class SubmissionResponse {
    private Long id;
    private UserSummaryResponse user;
    private ProblemSummaryResponse problem;
    private String code;
    private ProgrammingLanguage language;
    private SubmissionStatus status;
    private Double score;
    private Long executionTimeMs;
    private Long memoryUsageBytes;
    private List<TestResultResponse> testResults;
    private LocalDateTime submittedAt;
}