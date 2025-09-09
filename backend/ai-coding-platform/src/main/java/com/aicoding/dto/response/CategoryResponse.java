package com.aicoding.dto.response;

import com.aicoding.model.CategoryType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private CategoryType type;
    private int problemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
}