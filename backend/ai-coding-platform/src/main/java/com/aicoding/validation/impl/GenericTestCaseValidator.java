package com.aicoding.validation.impl;

import com.aicoding.config.AIProblemConfig;
import com.aicoding.validation.TestCaseValidator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenericTestCaseValidator implements TestCaseValidator {
    
    @Override
    public Map<String, Object> validateTestCase(String problemId, AIProblemConfig.TestCase testCase, String userCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("input", testCase.getInput());
        result.put("expected", testCase.getExpected());
        
        // Simulate execution based on problem type
        String actualResult = simulateExecution(problemId, testCase.getInput());
        result.put("actual", actualResult);
        
        boolean passed = actualResult.equals(testCase.getExpected());
        result.put("passed", passed);
        
        if (!passed) {
            result.put("feedback", String.format("Expected '%s' but got '%s'", 
                testCase.getExpected(), actualResult));
        }
        
        return result;
    }
    
    private String simulateExecution(String problemId, String input) {
        // Simulate different problem types
        switch (problemId) {
            case "recommendation-system":
                return "[\"Item1\", \"Item2\", \"Item3\"]";
            case "image-classification":
                return "cat";
            case "chatbot-nlp":
                return "Hello! How can I help you?";
            case "style-transfer":
                return "style_transferred_image.jpg";
            default:
                return "simulated_result";
        }
    }
    
    @Override
    public boolean supports(String problemId) {
        // This is the fallback validator for all problems
        return true;
    }
}