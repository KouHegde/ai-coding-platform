package com.aicoding.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aicoding.dto.request.ProblemRequest;
import com.aicoding.dto.request.TestCaseRequest;
import com.aicoding.dto.response.CategoryResponse;
import com.aicoding.dto.response.ProblemResponse;
import com.aicoding.dto.response.ProblemSummaryResponse;
import com.aicoding.dto.response.TestCaseResponse;
import com.aicoding.dto.response.UserSummaryResponse;
import com.aicoding.exception.ResourceNotFoundException;
import com.aicoding.model.Category;
import com.aicoding.model.CodeTemplate;
import com.aicoding.model.Difficulty;
import com.aicoding.model.Problem;
import com.aicoding.model.SubmissionStatus;
import com.aicoding.model.TestCase;
import com.aicoding.model.User;
import com.aicoding.repository.CategoryRepository;
import com.aicoding.repository.ProblemRepository;
import com.aicoding.repository.SubmissionRepository;
import com.aicoding.repository.TestCaseRepository;
import com.aicoding.security.services.UserDetailsImpl;
import com.aicoding.service.ProblemService;
import com.aicoding.service.UserService;


@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TestCaseRepository testCaseRepository;
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public ProblemResponse createProblem(ProblemRequest problemRequest) {
        Problem problem = new Problem();
        problem.setTitle(problemRequest.getTitle());
        problem.setDescription(problemRequest.getDescription());
        problem.setDifficulty(problemRequest.getDifficulty());
        problem.setActive(problemRequest.isActive());
        problem.setCreatedAt(LocalDateTime.now());
        problem.setUpdatedAt(LocalDateTime.now());
        
        // Set categories
        Set<Category> categories = problemRequest.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)))
                .collect(Collectors.toSet());
        problem.setCategories(categories);
        
        // Set code templates
        if (problemRequest.getCodeTemplates() != null) {
            List<CodeTemplate> codeTemplates = problemRequest.getCodeTemplates();
            for (CodeTemplate template : codeTemplates) {
                template.setProblem(problem);
            }
            problem.setCodeTemplates(codeTemplates);
        }

        // Set created by (current user)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userService.findById(userDetails.getId());
        problem.setCreatedBy(currentUser);

        // Save problem first to get ID
        Problem savedProblem = problemRepository.save(problem);

        // Create and save test cases
        if (problemRequest.getTestCases() != null) {
            List<TestCase> testCases = new ArrayList<>();
            for (TestCaseRequest testCaseRequest : problemRequest.getTestCases()) {
                TestCase testCase = new TestCase();
                testCase.setProblem(savedProblem);
                testCase.setInput(testCaseRequest.getInput());
                testCase.setExpectedOutput(testCaseRequest.getExpectedOutput());
                testCase.setHidden(testCaseRequest.isHidden());
                testCase.setOrderIndex(testCaseRequest.getOrderIndex());
                testCase.setMatchType(testCaseRequest.getMatchType());
                testCases.add(testCase);
            }
            testCaseRepository.saveAll(testCases);
            savedProblem.setTestCases(testCases);
        }

        return convertToProblemResponse(savedProblem);
    }

    @Override
    @Transactional
    public ProblemResponse updateProblem(Long id, ProblemRequest problemRequest) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + id));

        problem.setTitle(problemRequest.getTitle());
        problem.setDescription(problemRequest.getDescription());
        problem.setDifficulty(problemRequest.getDifficulty());
        problem.setActive(problemRequest.isActive());
        problem.setUpdatedAt(LocalDateTime.now());

        // Update categories
        Set<Category> categories = problemRequest.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId)))
                .collect(Collectors.toSet());
        problem.setCategories(categories);

        // Update code templates
        if (problemRequest.getCodeTemplates() != null) {
            // Clear existing code templates
            problem.getCodeTemplates().clear();

            // Add new code templates
            List<CodeTemplate> codeTemplates = problemRequest.getCodeTemplates();
            for (CodeTemplate template : codeTemplates) {
                template.setProblem(problem);
            }
            problem.setCodeTemplates(codeTemplates);
        }

        // Update test cases
        if (problemRequest.getTestCases() != null) {
            // Delete existing test cases
            testCaseRepository.deleteByProblemId(id);
            
            // Create new test cases
            List<TestCase> testCases = new ArrayList<>();
            for (TestCaseRequest testCaseRequest : problemRequest.getTestCases()) {
                TestCase testCase = new TestCase();
                testCase.setProblem(problem);
                testCase.setInput(testCaseRequest.getInput());
                testCase.setExpectedOutput(testCaseRequest.getExpectedOutput());
                testCase.setHidden(testCaseRequest.isHidden());
                testCase.setOrderIndex(testCaseRequest.getOrderIndex());
                testCase.setMatchType(testCaseRequest.getMatchType());
                testCases.add(testCase);
            }
            testCaseRepository.saveAll(testCases);
            problem.setTestCases(testCases);
        }
        
        Problem updatedProblem = problemRepository.save(problem);
        return convertToProblemResponse(updatedProblem);
    }

    @Override
    public ProblemResponse getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + id));
        return convertToProblemResponse(problem);
    }

    @Override
    public Page<ProblemSummaryResponse> getAllProblems(Pageable pageable) {
        Page<Problem> problemPage = problemRepository.findAll(pageable);
        return convertToProblemSummaryPage(problemPage);
    }

    @Override
    public Page<ProblemSummaryResponse> getActiveProblems(Pageable pageable) {
        Page<Problem> problemPage = problemRepository.findByActiveTrue(pageable);
        return convertToProblemSummaryPage(problemPage);
    }

    @Override
    public Page<ProblemSummaryResponse> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable) {
        Page<Problem> problemPage = problemRepository.findByDifficultyAndActiveTrue(difficulty, pageable);
        return convertToProblemSummaryPage(problemPage);
    }

    @Override
    public Page<ProblemSummaryResponse> getProblemsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        Page<Problem> problemPage = problemRepository.findByCategoriesContainingAndActiveTrue(category, pageable);
        return convertToProblemSummaryPage(problemPage);
    }

    @Override
    public List<ProblemSummaryResponse> getRecentProblems(int limit) {
        List<Problem> problems = problemRepository.findTop10ByActiveTrueOrderByCreatedAtDesc();
        if (limit > 0 && limit < problems.size()) {
            problems = problems.subList(0, limit);
        }
        return problems.stream()
                .map(this::convertToProblemSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Problem not found with id: " + id);
        }
        testCaseRepository.deleteByProblemId(id);
        problemRepository.deleteById(id);
    }

    @Override
    public void toggleProblemStatus(Long id, boolean active) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + id));
        problem.setActive(active);
        problem.setUpdatedAt(LocalDateTime.now());
        problemRepository.save(problem);
    }
    
    private ProblemResponse convertToProblemResponse(Problem problem) {
        ProblemResponse response = new ProblemResponse();
        response.setId(problem.getId());
        response.setTitle(problem.getTitle());
        response.setDescription(problem.getDescription());
        response.setDifficulty(problem.getDifficulty());
        response.setCreatedAt(problem.getCreatedAt());
        response.setUpdatedAt(problem.getUpdatedAt());
        response.setActive(problem.isActive());
        response.setCodeTemplates(problem.getCodeTemplates());
        
        // Set categories
        List<CategoryResponse> categoryResponses = problem.getCategories().stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());
        response.setCategories(categoryResponses);
        
        // Set test cases
        List<TestCaseResponse> testCaseResponses = problem.getTestCases().stream()
                .map(this::convertToTestCaseResponse)
                .collect(Collectors.toList());
        response.setTestCases(testCaseResponses);
        
        // Set created by user
        if (problem.getCreatedBy() != null) {
            UserSummaryResponse userResponse = new UserSummaryResponse();
            userResponse.setId(problem.getCreatedBy().getId());
            userResponse.setUsername(problem.getCreatedBy().getUsername());
            userResponse.setEmail(problem.getCreatedBy().getEmail());
            response.setCreatedBy(userResponse);
        }
        
        // Set submission statistics
        long submissionCount = submissionRepository.countByProblem(problem);
        long acceptedSubmissions = submissionRepository.countByProblemAndStatus(problem, SubmissionStatus.ACCEPTED);
        
        response.setSubmissionCount((int) submissionCount);
        if (submissionCount > 0) {
            response.setSuccessRate((double) acceptedSubmissions / submissionCount * 100);
        } else {
            response.setSuccessRate(0.0);
        }
        
        return response;
    }
    
    private ProblemSummaryResponse convertToProblemSummary(Problem problem) {
        ProblemSummaryResponse summary = new ProblemSummaryResponse();
        summary.setId(problem.getId());
        summary.setTitle(problem.getTitle());
        summary.setDifficulty(problem.getDifficulty());
        
        // Set category names
        List<String> categoryNames = problem.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        summary.setCategoryNames(categoryNames);
        
        // Set submission statistics
        long submissionCount = submissionRepository.countByProblem(problem);
        long acceptedSubmissions = submissionRepository.countByProblemAndStatus(problem, SubmissionStatus.ACCEPTED);
        
        summary.setSubmissionCount((int) submissionCount);
        if (submissionCount > 0) {
            summary.setSuccessRate((double) acceptedSubmissions / submissionCount * 100);
        } else {
            summary.setSuccessRate(0.0);
        }
        
        return summary;
    }
    
    private CategoryResponse convertToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setType(category.getType());
        response.setProblemCount(problemRepository.countByCategoriesContaining(category));
        return response;
    }
    
    private TestCaseResponse convertToTestCaseResponse(TestCase testCase) {
        TestCaseResponse response = new TestCaseResponse();
        response.setId(testCase.getId());
        response.setInput(testCase.getInput());
        response.setExpectedOutput(testCase.getExpectedOutput());
        response.setHidden(testCase.isHidden());
        response.setOrderIndex(testCase.getOrderIndex());
        response.setMatchType(testCase.getMatchType());
        
        // Mask hidden test cases
        if (testCase.isHidden()) {
            response.maskIfHidden();
        }
        
        return response;
    }
    
    private Page<ProblemSummaryResponse> convertToProblemSummaryPage(Page<Problem> problemPage) {
        List<ProblemSummaryResponse> problemSummaries = problemPage.getContent().stream()
                .map(this::convertToProblemSummary)
                .collect(Collectors.toList());
        
        return new PageImpl<>(problemSummaries, problemPage.getPageable(), problemPage.getTotalElements());
    }
}