package com.aicoding.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aicoding.dto.request.ProblemRequest;
import com.aicoding.dto.response.ProblemResponse;
import com.aicoding.dto.response.ProblemSummaryResponse;
import com.aicoding.model.Difficulty;

public interface ProblemService {
    ProblemResponse createProblem(ProblemRequest problemRequest);
    ProblemResponse updateProblem(Long id, ProblemRequest problemRequest);
    ProblemResponse getProblemById(Long id);
    Page<ProblemSummaryResponse> getAllProblems(Pageable pageable);
    Page<ProblemSummaryResponse> getActiveProblems(Pageable pageable);
    Page<ProblemSummaryResponse> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable);
    Page<ProblemSummaryResponse> getProblemsByCategory(Long categoryId, Pageable pageable);
    List<ProblemSummaryResponse> getRecentProblems(int limit);
    void deleteProblem(Long id);
    void toggleProblemStatus(Long id, boolean active);
}