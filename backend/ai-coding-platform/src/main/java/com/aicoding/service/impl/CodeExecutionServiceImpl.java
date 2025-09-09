package com.aicoding.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aicoding.dto.response.TestResultResponse;
import com.aicoding.model.MatchType;
import com.aicoding.model.ProgrammingLanguage;
import com.aicoding.model.Problem;
import com.aicoding.model.Submission;
import com.aicoding.model.SubmissionStatus;
import com.aicoding.model.TestCase;
import com.aicoding.model.TestResult;
import com.aicoding.repository.SubmissionRepository;
import com.aicoding.repository.TestResultRepository;
import com.aicoding.service.CodeExecutionService;

@Service
public class CodeExecutionServiceImpl implements CodeExecutionService {

    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private TestResultRepository testResultRepository;

    @Override
    public List<TestResultResponse> executeCode(String code, ProgrammingLanguage language, Problem problem) {
        List<TestResultResponse> results = new ArrayList<>();
        
        for (TestCase testCase : problem.getTestCases()) {
            TestResultResponse result = executeTestCase(code, language, testCase);
            results.add(result);
        }
        
        return results;
    }

    @Override
    public TestResultResponse executeTestCase(String code, ProgrammingLanguage language, TestCase testCase) {
        // This is a placeholder implementation
        // In a real implementation, this would execute the code in a sandbox environment
        // and return the actual results
        
        TestResultResponse result = new TestResultResponse();
        result.setTestCaseId(testCase.getId());
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());
        
        try {
            // Simulate code execution
            // In a real implementation, this would be replaced with actual code execution
            String actualOutput = simulateCodeExecution(code, language, testCase.getInput());
            result.setActualOutput(actualOutput);
            
            // Check if the output matches the expected output
            boolean passed = checkOutput(actualOutput, testCase.getExpectedOutput(), testCase.getMatchType());
            result.setPassed(passed);
            
            // Simulate execution time and memory usage
            result.setExecutionTimeMs((long) (Math.random() * 1000)); // Random time between 0-1000ms
            result.setMemoryUsageBytes((long) (Math.random() * 10 * 1024 * 1024)); // Random memory between 0-10MB
            
            if (testCase.getMatchType() == MatchType.SIMILARITY) {
                result.setSimilarityScore(calculateSimilarity(actualOutput, testCase.getExpectedOutput()));
            }
        } catch (Exception e) {
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }

    @Override
    public Submission evaluateSubmission(Submission submission) {
        // Set status to running
        submission.setStatus(SubmissionStatus.RUNNING);
        submissionRepository.save(submission);
        
        try {
            // Get all test cases for the problem
            List<TestCase> testCases = submission.getProblem().getTestCases();
            List<TestResult> testResults = new ArrayList<>();
            
            int passedCount = 0;
            long totalExecutionTime = 0;
            long maxMemoryUsage = 0;
            
            // Execute each test case
            for (TestCase testCase : testCases) {
                TestResult testResult = new TestResult();
                testResult.setSubmission(submission);
                testResult.setTestCase(testCase);
                
                try {
                    // Simulate code execution
                    String actualOutput = simulateCodeExecution(submission.getCode(), submission.getLanguage(), testCase.getInput());
                    testResult.setActualOutput(actualOutput);
                    
                    // Check if the output matches the expected output
                    boolean passed = checkOutput(actualOutput, testCase.getExpectedOutput(), testCase.getMatchType());
                    testResult.setPassed(passed);
                    
                    if (passed) {
                        passedCount++;
                    }
                    
                    // Simulate execution time and memory usage
                    long executionTime = (long) (Math.random() * 1000); // Random time between 0-1000ms
                    long memoryUsage = (long) (Math.random() * 10 * 1024 * 1024); // Random memory between 0-10MB
                    
                    testResult.setExecutionTimeMs(executionTime);
                    testResult.setMemoryUsageBytes(memoryUsage);
                    
                    totalExecutionTime += executionTime;
                    maxMemoryUsage = Math.max(maxMemoryUsage, memoryUsage);
                    
                    if (testCase.getMatchType() == MatchType.SIMILARITY) {
                        testResult.setSimilarityScore(calculateSimilarity(actualOutput, testCase.getExpectedOutput()));
                    }
                } catch (Exception e) {
                    testResult.setPassed(false);
                    testResult.setErrorMessage(e.getMessage());
                }
                
                testResults.add(testResult);
            }
            
            // Save test results
            testResultRepository.saveAll(testResults);
            submission.setTestResults(testResults);
            
            // Update submission status and statistics
            if (passedCount == testCases.size()) {
                submission.setStatus(SubmissionStatus.ACCEPTED);
            } else {
                submission.setStatus(SubmissionStatus.WRONG_ANSWER);
            }
            
            submission.setScore((double) passedCount / testCases.size() * 100);
            submission.setExecutionTimeMs(totalExecutionTime);
            submission.setMemoryUsageBytes(maxMemoryUsage);
            
        } catch (Exception e) {
            submission.setStatus(SubmissionStatus.SYSTEM_ERROR);
        }
        
        // Save and return updated submission
        return submissionRepository.save(submission);
    }
    
    // Placeholder method to simulate code execution
    // In a real implementation, this would execute the code in a sandbox environment
    private String simulateCodeExecution(String code, ProgrammingLanguage language, String input) throws Exception {
        // Simulate execution delay
        TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 500));
        
        // This is just a placeholder implementation
        // In a real implementation, this would execute the code and return the actual output
        
        // For demonstration purposes, we'll return a simple output based on the input
        // In a real implementation, this would be replaced with actual code execution
        
        // Simulate some basic processing based on the language and input
        switch (language) {
            case JAVA:
                // Simulate Java execution
                if (code.contains("System.out.println")) {
                    return input.trim() + " processed by Java";
                } else if (code.contains("Exception")) {
                    throw new Exception("Java runtime error");
                }
                break;
                
            case PYTHON:
                // Simulate Python execution
                if (code.contains("print")) {
                    return input.trim() + " processed by Python";
                } else if (code.contains("raise")) {
                    throw new Exception("Python runtime error");
                }
                break;
                
            case JAVASCRIPT:
                // Simulate JavaScript execution
                if (code.contains("console.log")) {
                    return input.trim() + " processed by JavaScript";
                } else if (code.contains("throw")) {
                    throw new Exception("JavaScript runtime error");
                }
                break;
                
            default:
                // Default simulation
                return input.trim() + " processed";
        }
        
        return input.trim() + " processed";
    }
    
    // Check if the output matches the expected output based on the match type
    private boolean checkOutput(String actualOutput, String expectedOutput, MatchType matchType) {
        if (actualOutput == null) {
            return expectedOutput == null;
        }
        
        switch (matchType) {
            case EXACT:
                return actualOutput.equals(expectedOutput);
                
            case IGNORE_CASE:
                return actualOutput.equalsIgnoreCase(expectedOutput);
                
            case IGNORE_WHITESPACE:
                return actualOutput.replaceAll("\\s+", "").equals(expectedOutput.replaceAll("\\s+", ""));
                
            case NUMERIC:
                try {
                    double actual = Double.parseDouble(actualOutput.trim());
                    double expected = Double.parseDouble(expectedOutput.trim());
                    return Math.abs(actual - expected) < 1e-6;
                } catch (NumberFormatException e) {
                    return false;
                }
                
            case CONTAINS:
                return actualOutput.contains(expectedOutput);
                
            case REGEX:
                return actualOutput.matches(expectedOutput);
                
            case SIMILARITY:
                double similarity = calculateSimilarity(actualOutput, expectedOutput);
                return similarity >= 0.8; // 80% similarity threshold
                
            default:
                return actualOutput.equals(expectedOutput);
        }
    }
    
    // Calculate similarity between two strings (simple Levenshtein distance implementation)
    private double calculateSimilarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) {
            return 1.0; // Both strings are empty
        }
        
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - (double) distance / maxLength;
    }
    
    // Levenshtein distance implementation
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
}