package com.aicoding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aicoding.model.CodeTemplate;
import com.aicoding.model.Problem;
import com.aicoding.model.ProgrammingLanguage;

@Repository
public interface CodeTemplateRepository extends JpaRepository<CodeTemplate, Long> {
    
    /**
     * Find all code templates for a specific problem
     * 
     * @param problem the problem entity
     * @return list of code templates
     */
    List<CodeTemplate> findByProblem(Problem problem);
    
    /**
     * Find all code templates for a specific problem ID
     * 
     * @param problemId the problem ID
     * @return list of code templates
     */
    List<CodeTemplate> findByProblemId(Long problemId);
    
    /**
     * Find a code template for a specific problem and programming language
     * 
     * @param problem the problem entity
     * @param language the programming language
     * @return optional code template
     */
    Optional<CodeTemplate> findByProblemAndLanguage(Problem problem, ProgrammingLanguage language);
    
    /**
     * Find a code template for a specific problem ID and programming language
     * 
     * @param problemId the problem ID
     * @param language the programming language
     * @return optional code template
     */
    Optional<CodeTemplate> findByProblemIdAndLanguage(Long problemId, ProgrammingLanguage language);
    
    /**
     * Delete all code templates for a specific problem
     * 
     * @param problem the problem entity
     */
    void deleteByProblem(Problem problem);
    
    /**
     * Delete all code templates for a specific problem ID
     * 
     * @param problemId the problem ID
     */
    void deleteByProblemId(Long problemId);
    
    /**
     * Check if a code template exists for a specific problem and programming language
     * 
     * @param problemId the problem ID
     * @param language the programming language
     * @return true if exists, false otherwise
     */
    boolean existsByProblemIdAndLanguage(Long problemId, ProgrammingLanguage language);
}