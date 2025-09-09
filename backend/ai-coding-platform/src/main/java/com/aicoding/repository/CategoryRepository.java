package com.aicoding.repository;

import com.aicoding.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    
    List<Category> findByType(com.aicoding.model.CategoryType type);
    
    Boolean existsByName(String name);
}