package com.runhub.ai.controller;

import com.runhub.ai.dto.AiRequest;
import com.runhub.ai.dto.AiResponse;
import com.runhub.ai.dto.ChatRequest;
import com.runhub.ai.dto.ChatResponse;
import com.runhub.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        String reply = aiService.chat(request.getMessage(), email);
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
