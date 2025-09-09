package com.aicoding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aicoding.model.Submission;
import com.aicoding.model.TestCase;
import com.aicoding.model.TestResult;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findBySubmission(Submission submission);
    
    List<TestResult> findBySubmissionAndPassed(Submission submission, boolean passed);
    
    List<TestResult> findByTestCase(TestCase testCase);
    
    @Modifying
    @Query("DELETE FROM TestResult tr WHERE tr.submission.id = :submissionId")
    void deleteBySubmissionId(Long submissionId);
    
    long countBySubmissionAndPassed(Submission submission, boolean passed);
}