package com.aicoding.service;

import java.util.List;

import com.aicoding.dto.response.UserSummaryResponse;
import com.aicoding.model.User;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
    UserSummaryResponse getUserSummary(Long userId);
    UserSummaryResponse getCurrentUserSummary();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}