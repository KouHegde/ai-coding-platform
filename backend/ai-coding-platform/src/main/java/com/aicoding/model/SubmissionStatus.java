package com.aicoding.model;

public enum SubmissionStatus {
    PENDING,        // Submission is queued for evaluation
    RUNNING,        // Submission is currently being evaluated
    ACCEPTED,       // All test cases passed
    WRONG_ANSWER,   // At least one test case failed
    COMPILE_ERROR,  // Code failed to compile
    RUNTIME_ERROR,  // Code threw an exception during execution
    TIME_LIMIT_EXCEEDED, // Code took too long to execute
    MEMORY_LIMIT_EXCEEDED, // Code used too much memory
    SYSTEM_ERROR    // Error in the evaluation system
}