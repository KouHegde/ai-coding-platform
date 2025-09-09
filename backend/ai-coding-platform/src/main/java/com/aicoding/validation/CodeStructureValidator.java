package com.aicoding.validation;

public interface CodeStructureValidator {
    /**
     * Validates the structure of user code
     * @param code The user's submitted code
     * @param problemId The problem ID
     * @return ValidationResult containing success status and error messages
     */
    ValidationResult validateStructure(String code, String problemId);
    
    /**
     * Checks if this validator supports the given problem type
     * @param problemId The problem ID to check
     * @return true if this validator can handle the problem
     */
    boolean supports(String problemId);
}