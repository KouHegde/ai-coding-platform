package com.aicoding.repository;

import com.aicoding.model.AIProblem;
import com.aicoding.model.Difficulty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIProblemRepository extends MongoRepository<AIProblem, String> {
    
    List<AIProblem> findByActiveTrue();
    
    long countByActiveTrue();
    
    List<AIProblem> findByActiveTrueAndCategory(String category);
    
    List<AIProblem> findByActiveTrueAndDifficulty(Difficulty difficulty);
    
    List<AIProblem> findByActiveTrueAndTagsContaining(String tag);
    
    @Query("{\"active\": true, \"category\": ?0, \"difficulty\": ?1}")
    List<AIProblem> findByActiveTrueAndCategoryAndDifficulty(String category, Difficulty difficulty);
    
    @Query("{\"active\": true, \"title\": {$regex: ?0, $options: \"i\"}}")
    List<AIProblem> findByActiveTrueAndTitleContainingIgnoreCase(String title);
    
    Optional<AIProblem> findByIdAndActiveTrue(String id);
    
    boolean existsByTitle(String title);
}