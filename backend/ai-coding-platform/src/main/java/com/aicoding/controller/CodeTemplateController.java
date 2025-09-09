package com.aicoding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aicoding.dto.request.CodeTemplateRequest;
import com.aicoding.dto.response.CodeTemplateResponse;
import com.aicoding.dto.response.MessageResponse;
import com.aicoding.model.ProgrammingLanguage;
import com.aicoding.service.CodeTemplateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/code-templates")
public class CodeTemplateController {

    @Autowired
    private CodeTemplateService codeTemplateService;

    @PostMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CodeTemplateResponse> createCodeTemplate(
            @Valid @RequestBody CodeTemplateRequest codeTemplateRequest) {
        CodeTemplateResponse codeTemplate = codeTemplateService.createCodeTemplate(codeTemplateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(codeTemplate);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CodeTemplateResponse> getCodeTemplateById(@PathVariable Long id) {
        CodeTemplateResponse codeTemplate = codeTemplateService.getCodeTemplateById(id);
        return ResponseEntity.ok(codeTemplate);
    }

    @GetMapping("/problem/{problemId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<CodeTemplateResponse>> getCodeTemplatesByProblem(@PathVariable Long problemId) {
        List<CodeTemplateResponse> codeTemplates = codeTemplateService.getCodeTemplatesByProblem(problemId);
        return ResponseEntity.ok(codeTemplates);
    }

    @GetMapping("/problem/{problemId}/language/{language}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CodeTemplateResponse> getCodeTemplateByProblemAndLanguage(
            @PathVariable Long problemId, 
            @PathVariable ProgrammingLanguage language) {
        CodeTemplateResponse codeTemplate = codeTemplateService.getCodeTemplateByProblemAndLanguage(problemId, language);
        return ResponseEntity.ok(codeTemplate);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CodeTemplateResponse> updateCodeTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CodeTemplateRequest codeTemplateRequest) {
        CodeTemplateResponse codeTemplate = codeTemplateService.updateCodeTemplate(id, codeTemplateRequest);
        return ResponseEntity.ok(codeTemplate);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteCodeTemplate(@PathVariable Long id) {
        codeTemplateService.deleteCodeTemplate(id);
        return ResponseEntity.ok(new MessageResponse("Code template deleted successfully"));
    }
}