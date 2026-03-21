package com.runhub.running.controller;

import com.runhub.running.dto.CreateExportTemplateRequest;
import com.runhub.running.dto.ExportTemplateDto;
import com.runhub.running.service.ExportTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/export-templates")
public class ExportTemplateController {

    private final ExportTemplateService service;

    public ExportTemplateController(ExportTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExportTemplateDto>> getAll(Principal principal) {
        return ResponseEntity.ok(service.getAllTemplates(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<ExportTemplateDto> create(Principal principal,
                                                      @Valid @RequestBody CreateExportTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTemplate(principal.getName(), request));
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<ExportTemplateDto> toggleVote(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(service.toggleVote(principal.getName(), id));
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<ExportTemplateDto> download(@PathVariable Long id) {
        return ResponseEntity.ok(service.incrementDownloads(id));
    }
}
