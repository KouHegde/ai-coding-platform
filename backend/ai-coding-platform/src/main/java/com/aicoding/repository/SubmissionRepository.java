package com.aicoding.repository;

import com.aicoding.model.Problem;
import com.aicoding.model.Submission;
import com.aicoding.model.SubmissionStatus;
import com.aicoding.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Page<Submission> findByUser(User user, Pageable pageable);
    
    Page<Submission> findByProblem(Problem problem, Pageable pageable);
    
    Page<Submission> findByUserAndProblem(User user, Problem problem, Pageable pageable);
    
    List<Submission> findByUserAndProblemAndStatus(User user, Problem problem, SubmissionStatus status);
    
    Page<Submission> findByUserAndProblemAndStatus(User user, Problem problem, SubmissionStatus status, Pageable pageable);
    
    Long countByProblem(Problem problem);
    
    Long countByUser(User user);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.problem = :problem AND s.status = :status")
    Long countByProblemAndStatus(Problem problem, SubmissionStatus status);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.user = :user AND s.status = :status")
    Long countByUserAndStatus(User user, SubmissionStatus status);

    @Query("SELECT COUNT(DISTINCT s.problem) FROM Submission s WHERE s.user = :user AND s.status = :status")
    Integer countDistinctProblemsByUserAndStatus(User user, SubmissionStatus status);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.submittedAt >= :startDate")
    Long countSubmissionsAfterDate(LocalDateTime startDate);
    
    @Query("SELECT s.problem.id, COUNT(s) as count FROM Submission s GROUP BY s.problem.id ORDER BY count DESC")
    List<Object[]> findMostAttemptedProblems(Pageable pageable);
}