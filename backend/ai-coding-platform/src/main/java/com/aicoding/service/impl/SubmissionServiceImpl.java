package com.aicoding.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aicoding.dto.request.SubmissionRequest;
import com.aicoding.dto.response.ProblemSummaryResponse;
import com.aicoding.dto.response.SubmissionResponse;
import com.aicoding.dto.response.TestResultResponse;
import com.aicoding.dto.response.UserSummaryResponse;
import com.aicoding.exception.ResourceNotFoundException;
import com.aicoding.model.Problem;
import com.aicoding.model.Submission;
import com.aicoding.model.SubmissionStatus;
import com.aicoding.model.TestResult;
import com.aicoding.model.User;
import com.aicoding.repository.ProblemRepository;
import com.aicoding.repository.SubmissionRepository;
import com.aicoding.repository.TestResultRepository;
import com.aicoding.security.services.UserDetailsImpl;
import com.aicoding.service.CodeExecutionService;
import com.aicoding.service.SubmissionService;
import com.aicoding.service.UserService;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private TestResultRepository testResultRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CodeExecutionService codeExecutionService;

    @Override
    @Transactional
    public SubmissionResponse submitSolution(SubmissionRequest submissionRequest) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userService.findById(userDetails.getId());
        
        // Get problem
        Problem problem = problemRepository.findById(submissionRequest.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + submissionRequest.getProblemId()));
        
        // Create submission
        Submission submission = new Submission();
        submission.setUser(currentUser);
        submission.setProblem(problem);
        submission.setCode(submissionRequest.getCode());
        submission.setLanguage(submissionRequest.getLanguage());
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setSubmittedAt(LocalDateTime.now());
        
        // Save submission to get ID
        Submission savedSubmission = submissionRepository.save(submission);
        
        // Execute code and evaluate submission
        Submission evaluatedSubmission = codeExecutionService.evaluateSubmission(savedSubmission);
        
        return convertToSubmissionResponse(evaluatedSubmission);
    }

    @Override
    public SubmissionResponse getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));
        return convertToSubmissionResponse(submission);
    }

    @Override
    public Page<SubmissionResponse> getUserSubmissions(Long userId, Pageable pageable) {
        User user = userService.findById(userId);
        Page<Submission> submissionPage = submissionRepository.findByUser(user, pageable);
        return convertToSubmissionResponsePage(submissionPage);
    }

    @Override
    public Page<SubmissionResponse> getProblemSubmissions(Long problemId, Pageable pageable) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + problemId));
        Page<Submission> submissionPage = submissionRepository.findByProblem(problem, pageable);
        return convertToSubmissionResponsePage(submissionPage);
    }

    @Override
    public Page<SubmissionResponse> getUserProblemSubmissions(Long userId, Long problemId, Pageable pageable) {
        User user = userService.findById(userId);
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + problemId));
        Page<Submission> submissionPage = submissionRepository.findByUserAndProblem(user, problem, pageable);
        return convertToSubmissionResponsePage(submissionPage);
    }

    @Override
    public Page<SubmissionResponse> getUserProblemSubmissionsByStatus(Long userId, Long problemId, SubmissionStatus status, Pageable pageable) {
        User user = userService.findById(userId);
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + problemId));
        Page<Submission> submissionPage = submissionRepository.findByUserAndProblemAndStatus(user, problem, status, pageable);
        return convertToSubmissionResponsePage(submissionPage);
    }

    @Override
    @Transactional
    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Submission not found with id: " + id);
        }
        testResultRepository.deleteBySubmissionId(id);
        submissionRepository.deleteById(id);
    }
    
    private SubmissionResponse convertToSubmissionResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setCode(submission.getCode());
        response.setLanguage(submission.getLanguage());
        response.setStatus(submission.getStatus());
        response.setScore(submission.getScore());
        response.setExecutionTimeMs(submission.getExecutionTimeMs());
        response.setMemoryUsageBytes(submission.getMemoryUsageBytes());
        response.setSubmittedAt(submission.getSubmittedAt());
        
        // Set user
        if (submission.getUser() != null) {
            UserSummaryResponse userResponse = new UserSummaryResponse();
            userResponse.setId(submission.getUser().getId());
            userResponse.setUsername(submission.getUser().getUsername());
            userResponse.setEmail(submission.getUser().getEmail());
            response.setUser(userResponse);
        }
        
        // Set problem
        if (submission.getProblem() != null) {
            ProblemSummaryResponse problemResponse = new ProblemSummaryResponse();
            problemResponse.setId(submission.getProblem().getId());
            problemResponse.setTitle(submission.getProblem().getTitle());
            problemResponse.setDifficulty(submission.getProblem().getDifficulty());
            response.setProblem(problemResponse);
        }
        
        // Set test results
        if (submission.getTestResults() != null) {
            List<TestResultResponse> testResultResponses = submission.getTestResults().stream()
                    .map(this::convertToTestResultResponse)
                    .collect(Collectors.toList());
            response.setTestResults(testResultResponses);
        }
        
        return response;
    }
    
    private TestResultResponse convertToTestResultResponse(TestResult testResult) {
        TestResultResponse response = new TestResultResponse();
        response.setId(testResult.getId());
        response.setTestCaseId(testResult.getTestCase().getId());
        response.setInput(testResult.getTestCase().getInput());
        response.setExpectedOutput(testResult.getTestCase().getExpectedOutput());
        response.setActualOutput(testResult.getActualOutput());
        response.setPassed(testResult.isPassed());
        response.setErrorMessage(testResult.getErrorMessage());
        response.setExecutionTimeMs(testResult.getExecutionTimeMs());
        response.setMemoryUsageBytes(testResult.getMemoryUsageBytes());
        response.setSimilarityScore(testResult.getSimilarityScore());
        return response;
    }
    
    private Page<SubmissionResponse> convertToSubmissionResponsePage(Page<Submission> submissionPage) {
        List<SubmissionResponse> submissionResponses = submissionPage.getContent().stream()
                .map(this::convertToSubmissionResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(submissionResponses, submissionPage.getPageable(), submissionPage.getTotalElements());
    }
}