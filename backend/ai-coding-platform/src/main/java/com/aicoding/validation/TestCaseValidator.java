package com.aicoding.validation;

import com.aicoding.config.AIProblemConfig;
import java.util.Map;

public interface TestCaseValidator {
    /**
     * Validates a single test case for a given problem
     * @param problemId The ID of the problem
     * @param testCase The test case to validate
     * @param userCode The user's submitted code
     * @return Test result containing input, expected, actual, and passed status
     */
    Map<String, Object> validateTestCase(String problemId, AIProblemConfig.TestCase testCase, String userCode);
    
    /**
     * Checks if this validator supports the given problem type
     * @param problemId The problem ID to check
     * @return true if this validator can handle the problem
     */
    boolean supports(String problemId);
}