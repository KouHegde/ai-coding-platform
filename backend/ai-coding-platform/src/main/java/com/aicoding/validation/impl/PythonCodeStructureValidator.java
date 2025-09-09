package com.aicoding.validation.impl;

import com.aicoding.validation.CodeStructureValidator;
import com.aicoding.validation.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class PythonCodeStructureValidator implements CodeStructureValidator {
    
    private static final Map<String, List<String>> REQUIRED_FUNCTIONS = new HashMap<>();
    private static final Map<String, List<String>> REQUIRED_PATTERNS = new HashMap<>();
    
    static {
        // Define required functions for each problem type
        REQUIRED_FUNCTIONS.put("sentiment-analysis", Arrays.asList("predict_sentiment", "analyze_sentiment"));
        REQUIRED_FUNCTIONS.put("recommendation-system", Arrays.asList("recommend", "get_recommendations"));
        REQUIRED_FUNCTIONS.put("image-classification", Arrays.asList("classify", "predict", "classify_image"));
        REQUIRED_FUNCTIONS.put("chatbot-nlp", Arrays.asList("respond", "generate_response", "chat"));
        REQUIRED_FUNCTIONS.put("style-transfer", Arrays.asList("transfer_style", "apply_style", "style_transfer"));
        
        // Define required patterns for each problem type
        REQUIRED_PATTERNS.put("sentiment-analysis", Arrays.asList("return", "def "));
        REQUIRED_PATTERNS.put("recommendation-system", Arrays.asList("return", "def "));
        REQUIRED_PATTERNS.put("image-classification", Arrays.asList("return", "def "));
        REQUIRED_PATTERNS.put("chatbot-nlp", Arrays.asList("return", "def "));
        REQUIRED_PATTERNS.put("style-transfer", Arrays.asList("return", "def "));
    }
    
    @Override
    public ValidationResult validateStructure(String code, String problemId) {
        ValidationResult result = new ValidationResult();
        
        // Basic validation
        if (code == null || code.trim().length() < 10) {
            result.addError("Code is too short or empty");
            return result;
        }
        
        // Check for basic Python syntax
        if (!code.contains("def ")) {
            result.addError("No function definition found. Please define at least one function.");
        }
        
        if (!code.contains("return")) {
            result.addWarning("No return statement found. Make sure your function returns a value.");
        }
        
        // Problem-specific validation
        List<String> requiredFunctions = REQUIRED_FUNCTIONS.get(problemId);
        if (requiredFunctions != null) {
            boolean hasRequiredFunction = requiredFunctions.stream()
                .anyMatch(func -> code.contains(func));
            
            if (!hasRequiredFunction) {
                result.addError(String.format("Missing required function. Expected one of: %s", 
                    String.join(", ", requiredFunctions)));
            }
        }
        
        // Check for common syntax errors
        validatePythonSyntax(code, result);
        
        return result;
    }
    
    private void validatePythonSyntax(String code, ValidationResult result) {
        // Check for proper indentation patterns
        String[] lines = code.split("\n");
        boolean inFunction = false;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            if (line.trim().startsWith("def ")) {
                inFunction = true;
                if (!line.trim().endsWith(":")) {
                    result.addError(String.format("Line %d: Function definition should end with ':'", i + 1));
                }
            }
            
            // Check for basic indentation in function body
            if (inFunction && i + 1 < lines.length) {
                String nextLine = lines[i + 1].trim();
                if (!nextLine.isEmpty() && !nextLine.startsWith("def ") && 
                    !lines[i + 1].startsWith("    ") && !lines[i + 1].startsWith("\t")) {
                    result.addWarning(String.format("Line %d: Consider proper indentation for function body", i + 2));
                }
            }
        }
    }
    
    @Override
    public boolean supports(String problemId) {
        // This validator supports all Python-based problems
        return true;
    }
}