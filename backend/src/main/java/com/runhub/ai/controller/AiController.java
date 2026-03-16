package com.runhub.ai.controller;

import com.runhub.ai.dto.AiRequest;
import com.runhub.ai.dto.AiResponse;
import com.runhub.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(@RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.ask(request.getQuestion()));
    }
}
