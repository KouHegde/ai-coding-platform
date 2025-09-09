package com.aicoding.validation.impl;

import com.aicoding.config.AIProblemConfig;
import com.aicoding.validation.TestCaseValidator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SentimentAnalysisValidator implements TestCaseValidator {
    
    @Override
    public Map<String, Object> validateTestCase(String problemId, AIProblemConfig.TestCase testCase, String userCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("input", testCase.getInput());
        result.put("expected", testCase.getExpected());
        
        // Execute sentiment analysis logic
        String actualResult = analyzeSentiment(testCase.getInput());
        result.put("actual", actualResult);
        
        boolean passed = actualResult.equals(testCase.getExpected());
        result.put("passed", passed);
        
        if (!passed) {
            result.put("feedback", generateFeedback(testCase.getInput(), testCase.getExpected(), actualResult));
        }
        
        return result;
    }
    
    private String analyzeSentiment(String text) {
        String lowerText = text.toLowerCase();
        
        String[] positiveWords = {"love", "best", "great", "excellent", "amazing", "wonderful", "fantastic", "good", "nice", "happy", "joy"};
        String[] negativeWords = {"hate", "worst", "terrible", "awful", "bad", "horrible", "sad", "angry", "disappointed", "stuck"};
        
        long positiveCount = Arrays.stream(positiveWords)
            .mapToLong(word -> countOccurrences(lowerText, word))
            .sum();
            
        long negativeCount = Arrays.stream(negativeWords)
            .mapToLong(word -> countOccurrences(lowerText, word))
            .sum();
        
        if (lowerText.contains("okay") || lowerText.contains("average") || lowerText.contains("nothing great")) {
            return "neutral";
        }
        
        if (positiveCount > negativeCount) {
            return "positive";
        } else if (negativeCount > positiveCount) {
            return "negative";
        } else {
            return "neutral";
        }
    }
    
    private long countOccurrences(String text, String word) {
        return Pattern.compile(Pattern.quote(word))
            .matcher(text)
            .results()
            .count();
    }
    
    private String generateFeedback(String input, String expected, String actual) {
        return String.format("For input '%s', expected '%s' but got '%s'. " +
            "Consider analyzing the emotional tone of the text more carefully.", 
            input, expected, actual);
    }
    
    @Override
    public boolean supports(String problemId) {
        return "sentiment-analysis".equals(problemId);
    }
}