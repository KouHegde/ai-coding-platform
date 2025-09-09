package com.aicoding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.Map;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ai")
@PropertySources({
    @PropertySource(value = "classpath:ai-problems-config.yaml", factory = YamlPropertySourceFactory.class)
})
public class AIProblemYamlConfig {
    private Map<String, Map<String, Object>> problems;
    
    public Map<String, Map<String, Object>> getProblems() {
        return problems;
    }
    
    public void setProblems(Map<String, Map<String, Object>> problems) {
        this.problems = problems;
    }
}