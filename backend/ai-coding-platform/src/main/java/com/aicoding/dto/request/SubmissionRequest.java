package com.aicoding.dto.request;

import com.aicoding.model.ProgrammingLanguage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull
    private Long problemId;
    
    @NotBlank
    private String code;
    
    @NotNull
    private ProgrammingLanguage language;
}