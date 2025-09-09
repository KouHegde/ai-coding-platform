package com.aicoding.dto.request;

import com.aicoding.model.MatchType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestCaseRequest {
    @NotBlank
    private String input;

    @NotBlank
    private String expectedOutput;

    private boolean hidden = false;

    @NotNull
    private Integer orderIndex;

    private MatchType matchType = MatchType.EXACT;
}