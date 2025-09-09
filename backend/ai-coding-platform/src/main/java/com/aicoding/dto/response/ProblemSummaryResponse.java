package com.aicoding.dto.response;

import java.util.List;

import com.aicoding.model.Difficulty;

import lombok.Data;

@Data
public class ProblemSummaryResponse {
    private Long id;
    private String title;
    private Difficulty difficulty;
    private List<String> categoryNames;
    private int submissionCount;
    private double successRate;
}