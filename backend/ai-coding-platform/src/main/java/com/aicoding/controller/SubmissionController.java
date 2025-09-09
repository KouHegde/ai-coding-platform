package com.aicoding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aicoding.dto.request.SubmissionRequest;
import com.aicoding.dto.response.MessageResponse;
import com.aicoding.dto.response.SubmissionResponse;
import com.aicoding.dto.response.TestResultResponse;
import com.aicoding.model.SubmissionStatus;
import com.aicoding.security.services.UserDetailsImpl;
import com.aicoding.service.SubmissionService;

// Add these missing imports:
import com.aicoding.service.CodeExecutionService;
import com.aicoding.repository.ProblemRepository;
import com.aicoding.model.ProgrammingLanguage;
import com.aicoding.model.Problem;
import com.aicoding.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;
    
    @Autowired
    private CodeExecutionService codeExecutionService;
    
    @Autowired
    private ProblemRepository problemRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<SubmissionResponse> submitSolution(
            @Valid @RequestBody SubmissionRequest submissionRequest) {
        
        SubmissionResponse submission = submissionService.submitSolution(submissionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<SubmissionResponse> getSubmissionById(@PathVariable Long id) {
        SubmissionResponse submission = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<SubmissionResponse>> getUserSubmissions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        // Check if the user is requesting their own submissions or has admin/moderator role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!userDetails.getId().equals(userId) && 
            !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                                                 a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<SubmissionResponse> submissions = submissionService.getUserSubmissions(userId, pageable);
        
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/problem/{problemId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<SubmissionResponse>> getProblemSubmissions(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<SubmissionResponse> submissions = submissionService.getProblemSubmissions(problemId, pageable);
        
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/user/{userId}/problem/{problemId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<SubmissionResponse>> getUserProblemSubmissions(
            @PathVariable Long userId,
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        // Check if the user is requesting their own submissions or has admin/moderator role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!userDetails.getId().equals(userId) && 
            !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                                                 a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<SubmissionResponse> submissions = submissionService.getUserProblemSubmissions(userId, problemId, pageable);
        
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/user/{userId}/problem/{problemId}/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<SubmissionResponse>> getUserProblemSubmissionsByStatus(
            @PathVariable Long userId,
            @PathVariable Long problemId,
            @PathVariable SubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Check if the user is requesting their own submissions or has admin/moderator role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!userDetails.getId().equals(userId) && 
            !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                                                 a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionResponse> submissions = submissionService.getUserProblemSubmissionsByStatus(userId, problemId, status, pageable);
        
        return ResponseEntity.ok(submissions);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.ok(new MessageResponse("Submission deleted successfully"));
    }

    @PostMapping("/run")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> runCode(
            @Valid @RequestBody Map<String, Object> request) {
        
        Long problemId = Long.valueOf(request.get("problemId").toString());
        String code = (String) request.get("code");
        String languageStr = (String) request.get("language");
        
        try {
            ProgrammingLanguage language = ProgrammingLanguage.valueOf(languageStr.toUpperCase());
            
            // Get the problem
            Problem problem = problemRepository.findById(problemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + problemId));
            
            // Execute code without creating a submission
            List<TestResultResponse> testResults = codeExecutionService.executeCode(code, language, problem);
            
            Map<String, Object> response = new HashMap<>();
            response.put("testResults", testResults);
            response.put("success", testResults.stream().allMatch(TestResultResponse::isPassed));
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid programming language: " + languageStr);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to execute code: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}