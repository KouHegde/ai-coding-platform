package com.aicoding.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean success;
    private List<String> errors;
    private List<String> warnings;
    private String message;
    
    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.success = true;
    }
    
    public ValidationResult(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.success = false;
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}