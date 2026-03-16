package com.runhub.chat.controller;

import com.runhub.chat.dto.MessageDto;
import com.runhub.chat.dto.SendMessageRequest;
import com.runhub.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) Long eventId) {
        return ResponseEntity.ok(chatService.getMessages(communityId, eventId));
    }

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(Principal principal, @RequestBody SendMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(principal.getName(), request));
    }
}
