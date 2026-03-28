package com.runhub.ai.controller;

import com.runhub.ai.dto.PerformanceDto;
import com.runhub.ai.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping
    public ResponseEntity<PerformanceDto> getPerformance(Principal principal) {
        return ResponseEntity.ok(performanceService.getPerformance(principal.getName()));
    }
}
