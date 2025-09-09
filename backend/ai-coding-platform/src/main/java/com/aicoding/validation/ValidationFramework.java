package com.aicoding.validation;

import com.aicoding.config.AIProblemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidationFramework {
    
    @Autowired
    private List<TestCaseValidator> testCaseValidators;
    
    @Autowired
    private List<CodeStructureValidator> codeStructureValidators;
    
    /**
     * Validates the complete solution including code structure and test cases
     */
    public Map<String, Object> validateSolution(String problemId, String userCode, List<AIProblemConfig.TestCase> testCases) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. Validate code structure first
        ValidationResult structureResult = validateCodeStructure(userCode, problemId);
        if (!structureResult.isSuccess()) {
            return createErrorResponse(structureResult.getMessage(), structureResult.getErrors(), 0, testCases.size());
        }
        
        // 2. Execute test cases
        List<Map<String, Object>> testResults = executeTestCases(problemId, testCases, userCode);
        
        // 3. Calculate results
        int passedTests = (int) testResults.stream().mapToLong(tr -> (Boolean) tr.get("passed") ? 1 : 0).sum();
        int totalTests = testCases.size();
        
        // 4. Build response
        result.put("success", passedTests == totalTests);
        result.put("passedTests", passedTests);
        result.put("totalTests", totalTests);
        result.put("testResults", testResults);
        result.put("message", generateResultMessage(passedTests, totalTests));
        result.put("structureWarnings", structureResult.getWarnings());
        
        return result;
    }
    
    private ValidationResult validateCodeStructure(String code, String problemId) {
        for (CodeStructureValidator validator : codeStructureValidators) {
            if (validator.supports(problemId)) {
                return validator.validateStructure(code, problemId);
            }
        }
        
        // Fallback to basic validation
        ValidationResult result = new ValidationResult();
        if (code == null || code.trim().length() < 10) {
            result.addError("Code is too short or empty");
        }
        return result;
    }
    
    private List<Map<String, Object>> executeTestCases(String problemId, List<AIProblemConfig.TestCase> testCases, String userCode) {
        return testCases.stream()
            .map(testCase -> executeTestCase(problemId, testCase, userCode))
            .toList();
    }
    
    private Map<String, Object> executeTestCase(String problemId, AIProblemConfig.TestCase testCase, String userCode) {
        // Find the most specific validator for this problem
        for (TestCaseValidator validator : testCaseValidators) {
            if (validator.supports(problemId) && !validator.getClass().getSimpleName().contains("Generic")) {
                return validator.validateTestCase(problemId, testCase, userCode);
            }
        }
        
        // Fallback to generic validator
        for (TestCaseValidator validator : testCaseValidators) {
            if (validator.supports(problemId)) {
                return validator.validateTestCase(problemId, testCase, userCode);
            }
        }
        
        // Should never reach here, but just in case
        Map<String, Object> result = new HashMap<>();
        result.put("input", testCase.getInput());
        result.put("expected", testCase.getExpected());
        result.put("actual", "error");
        result.put("passed", false);
        result.put("feedback", "No validator found for this problem type");
        return result;
    }
    
    private Map<String, Object> createErrorResponse(String message, List<String> errors, int passedTests, int totalTests) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("errors", errors);
        result.put("passedTests", passedTests);
        result.put("totalTests", totalTests);
        result.put("testResults", List.of());
        return result;
    }
    
    private String generateResultMessage(int passedTests, int totalTests) {
        if (passedTests == totalTests) {
            return "All tests passed! Great job!";
        } else {
            return String.format("Passed %d out of %d tests. Keep trying!", passedTests, totalTests);
        }
    }
}