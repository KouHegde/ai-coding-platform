package com.aicoding.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aicoding.dto.request.SubmissionRequest;
import com.aicoding.dto.response.SubmissionResponse;
import com.aicoding.model.SubmissionStatus;

public interface SubmissionService {
    SubmissionResponse submitSolution(SubmissionRequest submissionRequest);
    SubmissionResponse getSubmissionById(Long id);
    Page<SubmissionResponse> getUserSubmissions(Long userId, Pageable pageable);
    Page<SubmissionResponse> getProblemSubmissions(Long problemId, Pageable pageable);
    Page<SubmissionResponse> getUserProblemSubmissions(Long userId, Long problemId, Pageable pageable);
    Page<SubmissionResponse> getUserProblemSubmissionsByStatus(Long userId, Long problemId, SubmissionStatus status, Pageable pageable);
    void deleteSubmission(Long id);
}