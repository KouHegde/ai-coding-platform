package com.aicoding.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_problems")
public class AIProblem {
    @Id
    private String id;
    
    @NotBlank
    @Indexed
    private String title;
    
    @NotBlank
    private String description;
    
    @NotNull
    @Indexed
    private Difficulty difficulty;
    
    @NotBlank
    @Indexed
    private String category;
    
    @Indexed
    private List<String> tags = new ArrayList<>();
    
    private String starterCode;
    
    // Embedded test cases instead of separate collection
    private List<TestCase> testCases = new ArrayList<>();
    
    private String estimatedTime;
    private String acceptanceRate;
    
    @Indexed
    private boolean active = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Embedded TestCase class for MongoDB
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCase {
        private String input;
        private String expected;
        private boolean isHidden = false; // For hidden test cases
    }
}