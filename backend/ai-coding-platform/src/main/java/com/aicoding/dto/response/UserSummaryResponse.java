package com.aicoding.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserSummaryResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private boolean enabled;
    private int totalSubmissions;
    private int solvedProblems;
    private int problemsSolved;
    private double successRate;
}