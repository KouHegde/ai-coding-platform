package com.aicoding.dto.response;

import com.aicoding.model.MatchType;
import lombok.Data;

@Data
public class TestCaseResponse {
    private Long id;
    private String input;
    private String expectedOutput;
    private boolean hidden;
    private int orderIndex;
    private MatchType matchType;

    public void maskIfHidden() {
        if (hidden) {
            this.input = "Hidden test case";
            this.expectedOutput = "Hidden";
        }
    }
}