package com.aicoding.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.aicoding.dto.response.UserSummaryResponse;
import com.aicoding.exception.ResourceNotFoundException;
import com.aicoding.model.SubmissionStatus;
import com.aicoding.model.User;
import com.aicoding.repository.SubmissionRepository;
import com.aicoding.repository.UserRepository;
import com.aicoding.security.services.UserDetailsImpl;
import com.aicoding.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserSummaryResponse getUserSummary(Long userId) {
        User user = findById(userId);
        return createUserSummaryResponse(user);
    }

    @Override
    public UserSummaryResponse getCurrentUserSummary() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = findById(userDetails.getId());
        return createUserSummaryResponse(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    private UserSummaryResponse createUserSummaryResponse(User user) {
        UserSummaryResponse response = new UserSummaryResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        response.setCreatedAt(user.getCreatedAt());
        response.setEnabled(user.isEnabled());
        
        // Calculate statistics
        long totalSubmissions = submissionRepository.countByUser(user);
        long acceptedSubmissions = submissionRepository.countByUserAndStatus(user, SubmissionStatus.ACCEPTED);
        
        response.setTotalSubmissions((int) totalSubmissions);
        response.setSolvedProblems((int) submissionRepository.countDistinctProblemsByUserAndStatus(user, SubmissionStatus.ACCEPTED));
        response.setProblemsSolved((int) submissionRepository.countDistinctProblemsByUserAndStatus(user, SubmissionStatus.ACCEPTED));
        
        if (totalSubmissions > 0) {
            response.setSuccessRate((double) acceptedSubmissions / totalSubmissions * 100);
        } else {
            response.setSuccessRate(0.0);
        }
        
        return response;
    }
}