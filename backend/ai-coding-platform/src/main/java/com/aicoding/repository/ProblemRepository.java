package com.aicoding.repository;

import com.aicoding.model.Category;
import com.aicoding.model.Difficulty;
import com.aicoding.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Page<Problem> findByActive(boolean active, Pageable pageable);
    
    Page<Problem> findByActiveTrue(Pageable pageable);
    
    Page<Problem> findByDifficulty(Difficulty difficulty, Pageable pageable);
    
    Page<Problem> findByDifficultyAndActiveTrue(Difficulty difficulty, Pageable pageable);
    
    Page<Problem> findByCategoriesContaining(Category category, Pageable pageable);
    
    Page<Problem> findByCategoriesContainingAndActiveTrue(Category category, Pageable pageable);
    
    @Query("SELECT p FROM Problem p JOIN p.categories c WHERE c.type = :categoryType")
    Page<Problem> findByCategoryType(com.aicoding.model.CategoryType categoryType, Pageable pageable);
    
    List<Problem> findTop10ByOrderByCreatedAtDesc();
    
    List<Problem> findTop10ByActiveTrueOrderByCreatedAtDesc();
    
    int countByCategoriesContaining(Category category);
}