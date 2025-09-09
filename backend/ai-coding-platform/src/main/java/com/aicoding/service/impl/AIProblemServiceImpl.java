package com.aicoding.service.impl;

import com.aicoding.config.AIProblemConfig;
import com.aicoding.dto.response.AIProblemResponse;
import com.aicoding.model.AIProblem;
import com.aicoding.model.Difficulty;
import com.aicoding.repository.AIProblemRepository;
import com.aicoding.service.AIProblemService;
import com.aicoding.validation.ValidationFramework;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AIProblemServiceImpl implements AIProblemService {
    
    @Autowired
    private AIProblemRepository aiProblemRepository;
    
    @Autowired
    private AIProblemConfig aiProblemConfig; // Keep as backup
    
    @Autowired
    private ValidationFramework validationFramework;
    
    @Override
    public List<AIProblem> getAllActiveProblems() {
        try {
            return aiProblemRepository.findByActiveTrue();
        } catch (Exception e) {
            log.warn("MongoDB not available, falling back to config: {}", e.getMessage());
            return getProblemsFromConfig();
        }
    }
    
    @Override
    public AIProblem getProblemById(String id) {
        try {
            Optional<AIProblem> problem = aiProblemRepository.findByIdAndActiveTrue(id);
            if (problem.isPresent()) {
                return problem.get();
            }
        } catch (Exception e) {
            log.warn("MongoDB not available, falling back to config: {}", e.getMessage());
        }
        
        // Fallback to config
        return getProblemFromConfig(id);
    }
    
    @Override
    public AIProblemResponse getProblemWithTestCases(String problemId) {
        try {
            Optional<AIProblem> problemOpt = aiProblemRepository.findByIdAndActiveTrue(problemId);
            if (problemOpt.isPresent()) {
                AIProblem problem = problemOpt.get();
                return convertToResponse(problem);
            }
        } catch (Exception e) {
            log.warn("MongoDB not available, falling back to config: {}", e.getMessage());
        }
        
        // Fallback to config
        return getProblemWithTestCasesFromConfig(problemId);
    }
    
    @Override
    public Map<String, Object> validateSolution(String problemId, String code) {
        try {
            Optional<AIProblem> problemOpt = aiProblemRepository.findByIdAndActiveTrue(problemId);
            if (problemOpt.isPresent()) {
                AIProblem problem = problemOpt.get();
                List<AIProblemConfig.TestCase> testCases = problem.getTestCases().stream()
                    .map(tc -> {
                        AIProblemConfig.TestCase configTC = new AIProblemConfig.TestCase();
                        configTC.setInput(tc.getInput());
                        configTC.setExpected(tc.getExpected());
                        return configTC;
                    })
                    .collect(Collectors.toList());
                
                return validationFramework.validateSolution(problemId, code, testCases);
            }
        } catch (Exception e) {
            log.warn("MongoDB not available, falling back to config: {}", e.getMessage());
        }
        
        // Fallback to config validation
        AIProblemConfig.Problem config = aiProblemConfig.getProblems().get(problemId);
        if (config == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Problem not found");
            result.put("passedTests", 0);
            result.put("totalTests", 0);
            result.put("testResults", new ArrayList<>());
            return result;
        }
        
        return validationFramework.validateSolution(problemId, code, config.getTestCases());
    }
    
    @Override
    public void initializeDefaultProblems() {
        log.info("Problem initialization is handled by DataInitializationService");
    }
    
    // Additional MongoDB-specific methods
    public List<AIProblem> getProblemsByCategory(String category) {
        return aiProblemRepository.findByActiveTrueAndCategory(category);
    }
    
    public List<AIProblem> getProblemsByDifficulty(Difficulty difficulty) {
        return aiProblemRepository.findByActiveTrueAndDifficulty(difficulty);
    }
    
    public List<AIProblem> getProblemsByTag(String tag) {
        return aiProblemRepository.findByActiveTrueAndTagsContaining(tag);
    }
    
    public List<AIProblem> searchProblems(String query) {
        return aiProblemRepository.findByActiveTrueAndTitleContainingIgnoreCase(query);
    }
    
    // Fallback methods for config
    private List<AIProblem> getProblemsFromConfig() {
        List<AIProblem> problems = new ArrayList<>();
        for (Map.Entry<String, AIProblemConfig.Problem> entry : aiProblemConfig.getProblems().entrySet()) {
            problems.add(convertConfigToProblem(entry.getKey(), entry.getValue()));
        }
        return problems;
    }
    
    private AIProblem getProblemFromConfig(String id) {
        AIProblemConfig.Problem config = aiProblemConfig.getProblems().get(id);
        return config != null ? convertConfigToProblem(id, config) : null;
    }
    
    private AIProblemResponse getProblemWithTestCasesFromConfig(String problemId) {
        AIProblemConfig.Problem config = aiProblemConfig.getProblems().get(problemId);
        if (config == null) {
            return null;
        }
        
        AIProblemResponse response = new AIProblemResponse();
        response.setId(problemId);
        response.setTitle(config.getTitle());
        response.setDescription(config.getDescription());
        response.setDifficulty(config.getDifficulty());
        response.setCategory(config.getCategory());
        response.setStarterCode(config.getStarterCode());
        
        List<AIProblemResponse.TestCase> testCases = new ArrayList<>();
        for (AIProblemConfig.TestCase tc : config.getTestCases()) {
            AIProblemResponse.TestCase testCase = new AIProblemResponse.TestCase();
            testCase.setInput(tc.getInput());
            testCase.setExpected(tc.getExpected());
            testCases.add(testCase);
        }
        response.setTestCases(testCases);
        
        return response;
    }
    
    private AIProblem convertConfigToProblem(String id, AIProblemConfig.Problem config) {
        AIProblem problem = new AIProblem();
        problem.setId(id);
        problem.setTitle(config.getTitle());
        problem.setDescription(config.getDescription());
        problem.setDifficulty(Difficulty.valueOf(config.getDifficulty()));
        problem.setCategory(config.getCategory());
        problem.setTags(config.getTags());
        problem.setEstimatedTime(config.getEstimatedTime());
        problem.setAcceptanceRate(config.getAcceptanceRate());
        problem.setStarterCode(config.getStarterCode());
        problem.setActive(true);
        return problem;
    }
    
    private AIProblemResponse convertToResponse(AIProblem problem) {
        AIProblemResponse response = new AIProblemResponse();
        response.setId(problem.getId());
        response.setTitle(problem.getTitle());
        response.setDescription(problem.getDescription());
        response.setDifficulty(problem.getDifficulty().name());
        response.setCategory(problem.getCategory());
        response.setStarterCode(problem.getStarterCode());
        
        List<AIProblemResponse.TestCase> testCases = problem.getTestCases().stream()
            .filter(tc -> !tc.isHidden()) // Only return visible test cases
            .map(tc -> {
                AIProblemResponse.TestCase testCase = new AIProblemResponse.TestCase();
                testCase.setInput(tc.getInput());
                testCase.setExpected(tc.getExpected());
                return testCase;
            })
            .collect(Collectors.toList());
        response.setTestCases(testCases);
        
        return response;
    }
}