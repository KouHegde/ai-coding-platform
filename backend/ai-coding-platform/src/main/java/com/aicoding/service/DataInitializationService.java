package com.aicoding.service;

import com.aicoding.config.AIProblemConfig;
import com.aicoding.model.AIProblem;
import com.aicoding.model.Difficulty;
import com.aicoding.repository.AIProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private AIProblemRepository aiProblemRepository;
    
    @Autowired
    private AIProblemConfig aiProblemConfig;
    
    @Override
    public void run(String... args) throws Exception {
        initializeAIProblemsFromConfig();
    }
    
    public void initializeAIProblemsFromConfig() {
        log.info("Initializing AI problems from configuration...");
        
        for (Map.Entry<String, AIProblemConfig.Problem> entry : aiProblemConfig.getProblems().entrySet()) {
            String problemId = entry.getKey();
            AIProblemConfig.Problem config = entry.getValue();
            
            // Check if problem already exists
            if (aiProblemRepository.existsByTitle(config.getTitle())) {
                log.debug("Problem '{}' already exists, skipping...", config.getTitle());
                continue;
            }
            
            AIProblem problem = new AIProblem();
            problem.setId(problemId);
            problem.setTitle(config.getTitle());
            problem.setDescription(config.getDescription());
            problem.setDifficulty(Difficulty.valueOf(config.getDifficulty()));
            problem.setCategory(config.getCategory());
            problem.setTags(config.getTags() != null ? config.getTags() : new ArrayList<>());
            problem.setEstimatedTime(config.getEstimatedTime());
            problem.setAcceptanceRate(config.getAcceptanceRate());
            problem.setStarterCode(config.getStarterCode());
            problem.setActive(true);
            problem.setCreatedAt(LocalDateTime.now());
            problem.setUpdatedAt(LocalDateTime.now());
            
            // Convert test cases
            List<AIProblem.TestCase> testCases = new ArrayList<>();
            if (config.getTestCases() != null) {
                for (AIProblemConfig.TestCase tc : config.getTestCases()) {
                    AIProblem.TestCase testCase = new AIProblem.TestCase();
                    testCase.setInput(tc.getInput());
                    testCase.setExpected(tc.getExpected());
                    testCase.setHidden(false); // Default to visible
                    testCases.add(testCase);
                }
            }
            problem.setTestCases(testCases);
            
            aiProblemRepository.save(problem);
            log.info("Initialized problem: {}", config.getTitle());
        }
        
        log.info("AI problems initialization completed. Total problems: {}", aiProblemRepository.count());
    }
    
    public void reinitializeFromConfig() {
        log.info("Reinitializing all problems from configuration...");
        aiProblemRepository.deleteAll();
        initializeAIProblemsFromConfig();
    }
}