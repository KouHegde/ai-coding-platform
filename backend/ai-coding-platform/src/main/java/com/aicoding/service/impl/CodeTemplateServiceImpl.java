package com.aicoding.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aicoding.dto.request.CodeTemplateRequest;
import com.aicoding.dto.response.CodeTemplateResponse;
import com.aicoding.exception.ResourceNotFoundException;
import com.aicoding.model.CodeTemplate;
import com.aicoding.model.Problem;
import com.aicoding.model.ProgrammingLanguage;
import com.aicoding.repository.CodeTemplateRepository;
import com.aicoding.repository.ProblemRepository;
import com.aicoding.service.CodeTemplateService;

@Service
public class CodeTemplateServiceImpl implements CodeTemplateService {

    @Autowired
    private CodeTemplateRepository codeTemplateRepository;
    
    @Autowired
    private ProblemRepository problemRepository;

    @Override
    @Transactional
    public CodeTemplateResponse createCodeTemplate(CodeTemplateRequest codeTemplateRequest) {
        // Check if problem exists
        Problem problem = problemRepository.findById(codeTemplateRequest.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + codeTemplateRequest.getProblemId()));
        
        // Check if a template for this problem and language already exists
        if (codeTemplateRepository.existsByProblemIdAndLanguage(problem.getId(), codeTemplateRequest.getLanguage())) {
            throw new IllegalArgumentException("A template for this problem and language already exists");
        }
        
        // Create new code template
        CodeTemplate codeTemplate = new CodeTemplate();
        codeTemplate.setProblem(problem);
        codeTemplate.setLanguage(codeTemplateRequest.getLanguage());
        codeTemplate.setTemplateCode(codeTemplateRequest.getTemplateCode());
        codeTemplate.setFunctionSignature(codeTemplateRequest.getFunctionSignature());
        codeTemplate.setComments(codeTemplateRequest.getComments());
        
        // Save to database
        codeTemplate = codeTemplateRepository.save(codeTemplate);
        
        // Convert to response DTO
        return convertToResponse(codeTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public CodeTemplateResponse getCodeTemplateById(Long id) {
        CodeTemplate codeTemplate = codeTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Code template not found with id: " + id));
        
        return convertToResponse(codeTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodeTemplateResponse> getCodeTemplatesByProblem(Long problemId) {
        // Check if problem exists
        if (!problemRepository.existsById(problemId)) {
            throw new ResourceNotFoundException("Problem not found with id: " + problemId);
        }
        
        List<CodeTemplate> codeTemplates = codeTemplateRepository.findByProblemId(problemId);
        
        return codeTemplates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CodeTemplateResponse getCodeTemplateByProblemAndLanguage(Long problemId, ProgrammingLanguage language) {
        // Check if problem exists
        if (!problemRepository.existsById(problemId)) {
            throw new ResourceNotFoundException("Problem not found with id: " + problemId);
        }
        
        CodeTemplate codeTemplate = codeTemplateRepository.findByProblemIdAndLanguage(problemId, language)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Code template not found for problem id: " + problemId + " and language: " + language));
        
        return convertToResponse(codeTemplate);
    }

    @Override
    @Transactional
    public CodeTemplateResponse updateCodeTemplate(Long id, CodeTemplateRequest codeTemplateRequest) {
        // Check if code template exists
        CodeTemplate codeTemplate = codeTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Code template not found with id: " + id));
        
        // Check if problem exists
        Problem problem = problemRepository.findById(codeTemplateRequest.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + codeTemplateRequest.getProblemId()));
        
        // Check if updating to a different problem/language combination that already exists
        if (!codeTemplate.getProblem().getId().equals(problem.getId()) || 
                !codeTemplate.getLanguage().equals(codeTemplateRequest.getLanguage())) {
            
            if (codeTemplateRepository.existsByProblemIdAndLanguage(problem.getId(), codeTemplateRequest.getLanguage())) {
                throw new IllegalArgumentException("A template for this problem and language already exists");
            }
        }
        
        // Update code template
        codeTemplate.setProblem(problem);
        codeTemplate.setLanguage(codeTemplateRequest.getLanguage());
        codeTemplate.setTemplateCode(codeTemplateRequest.getTemplateCode());
        codeTemplate.setFunctionSignature(codeTemplateRequest.getFunctionSignature());
        codeTemplate.setComments(codeTemplateRequest.getComments());
        
        // Save to database
        codeTemplate = codeTemplateRepository.save(codeTemplate);
        
        // Convert to response DTO
        return convertToResponse(codeTemplate);
    }

    @Override
    @Transactional
    public void deleteCodeTemplate(Long id) {
        // Check if code template exists
        if (!codeTemplateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Code template not found with id: " + id);
        }
        
        codeTemplateRepository.deleteById(id);
    }
    
    /**
     * Helper method to convert CodeTemplate entity to CodeTemplateResponse DTO
     * 
     * @param codeTemplate the code template entity
     * @return the code template response DTO
     */
    private CodeTemplateResponse convertToResponse(CodeTemplate codeTemplate) {
        CodeTemplateResponse response = new CodeTemplateResponse();
        response.setId(codeTemplate.getId());
        response.setProblemId(codeTemplate.getProblem().getId());
        response.setProblemTitle(codeTemplate.getProblem().getTitle());
        response.setLanguage(codeTemplate.getLanguage());
        response.setTemplateCode(codeTemplate.getTemplateCode());
        response.setFunctionSignature(codeTemplate.getFunctionSignature());
        response.setComments(codeTemplate.getComments());
        response.setCreatedAt(codeTemplate.getCreatedAt());
        response.setUpdatedAt(codeTemplate.getUpdatedAt());
        return response;
    }
}