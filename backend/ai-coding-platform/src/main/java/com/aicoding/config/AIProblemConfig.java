package com.aicoding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
@PropertySource(value = "classpath:ai-problems-config.yaml", factory = YamlPropertySourceFactory.class)
public class AIProblemConfig {
    
    private Map<String, Problem> problems;
    
    @Data
    public static class Problem {
        private String title;
        private String description;
        private String difficulty;
        private String category;
        private List<String> tags;
        private String estimatedTime;
        private String acceptanceRate;
        private List<TestCase> testCases;
        private String starterCode;
    }
    
    @Data
    public static class TestCase {
        private String input;
        private String expected;
    }
}