package com.aicoding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aicoding.model.Problem;
import com.aicoding.model.TestCase;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProblem(Problem problem);
    
    List<TestCase> findByProblemOrderByOrderIndexAsc(Problem problem);
    
    @Modifying
    @Query("DELETE FROM TestCase t WHERE t.problem.id = :problemId")
    void deleteByProblemId(Long problemId);
    
    long countByProblem(Problem problem);
    
    long countByProblemAndIsHiddenTrue(Problem problem);
}