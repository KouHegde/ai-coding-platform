package com.aicoding.dto.request;

import java.util.List;

import com.aicoding.model.CodeTemplate;
import com.aicoding.model.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProblemRequest {
    @NotBlank
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank
    @Size(min = 20, max = 5000)
    private String description;

    @NotNull
    private Difficulty difficulty;

    @NotEmpty
    private List<Long> categoryIds;

    private List<TestCaseRequest> testCases;

    private List<CodeTemplate> codeTemplates;

    private boolean active = true;
}