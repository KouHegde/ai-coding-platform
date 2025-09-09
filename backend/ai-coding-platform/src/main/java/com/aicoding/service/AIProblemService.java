package com.aicoding.service;

import com.aicoding.dto.response.AIProblemResponse;
import com.aicoding.model.AIProblem;

import java.util.List;
import java.util.Map;

public interface AIProblemService {
    List<AIProblem> getAllActiveProblems();
    AIProblem getProblemById(String id);
    AIProblemResponse getProblemWithTestCases(String problemId);
    Map<String, Object> validateSolution(String problemId, String code);
    void initializeDefaultProblems();
}