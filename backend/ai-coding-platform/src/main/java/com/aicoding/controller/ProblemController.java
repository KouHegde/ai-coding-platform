package com.aicoding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aicoding.dto.request.ProblemRequest;
import com.aicoding.dto.response.MessageResponse;
import com.aicoding.dto.response.ProblemResponse;
import com.aicoding.dto.response.ProblemSummaryResponse;
import com.aicoding.model.Difficulty;
import com.aicoding.service.ProblemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping
    public ResponseEntity<Page<ProblemSummaryResponse>> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ProblemSummaryResponse> problems = problemService.getActiveProblems(pageable);
        
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<ProblemSummaryResponse>> getAllProblemsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ProblemSummaryResponse> problems = problemService.getAllProblems(pageable);
        
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblemById(@PathVariable Long id) {
        ProblemResponse problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Page<ProblemSummaryResponse>> getProblemsByDifficulty(
            @PathVariable Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProblemSummaryResponse> problems = problemService.getProblemsByDifficulty(difficulty, pageable);
        
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProblemSummaryResponse>> getProblemsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProblemSummaryResponse> problems = problemService.getProblemsByCategory(categoryId, pageable);
        
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ProblemSummaryResponse>> getRecentProblems(
            @RequestParam(defaultValue = "5") int limit) {
        
        List<ProblemSummaryResponse> problems = problemService.getRecentProblems(limit);
        return ResponseEntity.ok(problems);
    }

    @PostMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ProblemResponse> createProblem(@Valid @RequestBody ProblemRequest problemRequest) {
        ProblemResponse createdProblem = problemService.createProblem(problemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProblem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemRequest problemRequest) {
        
        ProblemResponse updatedProblem = problemService.updateProblem(id, problemRequest);
        return ResponseEntity.ok(updatedProblem);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> toggleProblemStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        
        problemService.toggleProblemStatus(id, active);
        String status = active ? "activated" : "deactivated";
        return ResponseEntity.ok(new MessageResponse("Problem has been " + status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.ok(new MessageResponse("Problem deleted successfully"));
    }
}