package com.aicoding.service;

import java.util.List;

import com.aicoding.dto.request.CodeTemplateRequest;
import com.aicoding.dto.response.CodeTemplateResponse;
import com.aicoding.model.ProgrammingLanguage;

public interface CodeTemplateService {

    /**
     * Creates a new code template
     * 
     * @param codeTemplateRequest the code template request
     * @return the created code template response
     */
    CodeTemplateResponse createCodeTemplate(CodeTemplateRequest codeTemplateRequest);
    
    /**
     * Gets a code template by its ID
     * 
     * @param id the code template ID
     * @return the code template response
     */
    CodeTemplateResponse getCodeTemplateById(Long id);
    
    /**
     * Gets all code templates for a specific problem
     * 
     * @param problemId the problem ID
     * @return list of code template responses
     */
    List<CodeTemplateResponse> getCodeTemplatesByProblem(Long problemId);
    
    /**
     * Gets a code template for a specific problem and programming language
     * 
     * @param problemId the problem ID
     * @param language the programming language
     * @return the code template response
     */
    CodeTemplateResponse getCodeTemplateByProblemAndLanguage(Long problemId, ProgrammingLanguage language);
    
    /**
     * Updates an existing code template
     * 
     * @param id the code template ID
     * @param codeTemplateRequest the updated code template request
     * @return the updated code template response
     */
    CodeTemplateResponse updateCodeTemplate(Long id, CodeTemplateRequest codeTemplateRequest);
    
    /**
     * Deletes a code template
     * 
     * @param id the code template ID
     */
    void deleteCodeTemplate(Long id);
}