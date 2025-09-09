package com.aicoding.service;

import java.util.List;

import com.aicoding.dto.response.TestResultResponse;
import com.aicoding.model.ProgrammingLanguage;
import com.aicoding.model.Problem;
import com.aicoding.model.Submission;
import com.aicoding.model.TestCase;

public interface CodeExecutionService {
    /**
     * Executes code against test cases and returns the results
     * 
     * @param code The source code to execute
     * @param language The programming language of the code
     * @param problem The problem containing test cases
     * @return List of test results
     */
    List<TestResultResponse> executeCode(String code, ProgrammingLanguage language, Problem problem);
    
    /**
     * Executes code against a specific test case
     * 
     * @param code The source code to execute
     * @param language The programming language of the code
     * @param testCase The test case to run
     * @return Test result
     */
    TestResultResponse executeTestCase(String code, ProgrammingLanguage language, TestCase testCase);
    
    /**
     * Evaluates a submission against all test cases and updates the submission status
     * 
     * @param submission The submission to evaluate
     * @return Updated submission with results
     */
    Submission evaluateSubmission(Submission submission);
}