package com.aicoding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aicoding.dto.response.UserSummaryResponse;
import com.aicoding.model.User;
import com.aicoding.security.services.UserDetailsImpl;
import com.aicoding.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<UserSummaryResponse> getCurrentUser() {
        UserSummaryResponse userSummary = userService.getCurrentUserSummary();
        return ResponseEntity.ok(userSummary);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<UserSummaryResponse> getUserById(@PathVariable Long id) {
        // Check if the user is requesting their own profile or has admin/moderator role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (userDetails.getId().equals(id) || 
            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                                                 a.getAuthority().equals("ROLE_MODERATOR"))) {
            UserSummaryResponse userSummary = userService.getUserSummary(id);
            return ResponseEntity.ok(userSummary);
        }
        
        // For regular users, return a limited view of other users
        UserSummaryResponse userSummary = userService.getUserSummary(id);
        // Remove sensitive information like email
        userSummary.setEmail(null);
        return ResponseEntity.ok(userSummary);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
}