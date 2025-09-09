package com.aicoding.controller;

import com.aicoding.dto.response.AIProblemResponse;
import com.aicoding.model.AIProblem;
import com.aicoding.service.AIProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-problems")
public class AIProblemController {
    
    @Autowired
    private AIProblemService aiProblemService;
    
    @GetMapping
    public ResponseEntity<List<AIProblem>> getAllProblems() {
        List<AIProblem> problems = aiProblemService.getAllActiveProblems();
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AIProblem> getProblemById(@PathVariable String id) {
        AIProblem problem = aiProblemService.getProblemById(id);
        if (problem != null) {
            return ResponseEntity.ok(problem);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<AIProblemResponse> getProblemWithTestCases(@PathVariable String id) {
        AIProblemResponse problem = aiProblemService.getProblemWithTestCases(id);
        if (problem != null) {
            return ResponseEntity.ok(problem);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitSolution(
            @PathVariable String id,
            @RequestBody Map<String, String> submission) {
        
        String code = submission.get("code");
        Map<String, Object> result = aiProblemService.validateSolution(id, code);
        return ResponseEntity.ok(result);
    }
}