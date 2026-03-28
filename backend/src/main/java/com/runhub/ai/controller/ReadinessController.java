package com.runhub.ai.controller;

import com.runhub.ai.dto.ReadinessDto;
import com.runhub.ai.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/readiness")
@RequiredArgsConstructor
public class ReadinessController {

    private final ReadinessService readinessService;

    @GetMapping
    public ResponseEntity<ReadinessDto> getReadiness(Principal principal) {
        return ResponseEntity.ok(readinessService.getReadiness(principal.getName()));
    }
}
