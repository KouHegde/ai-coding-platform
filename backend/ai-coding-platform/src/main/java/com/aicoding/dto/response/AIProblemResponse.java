package com.aicoding.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AIProblemResponse {
    private String id;
    private String title;
    private String description;
    private String difficulty;
    private String category;
    private String starterCode;
    private List<TestCase> testCases;
    
    @Data
    public static class TestCase {
        private String input;
        private String expected;
    }
}