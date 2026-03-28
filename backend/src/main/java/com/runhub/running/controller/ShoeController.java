package com.runhub.running.controller;

import com.runhub.running.service.ShoeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shoes")
@RequiredArgsConstructor
public class ShoeController {

    private final ShoeService shoeService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getShoes(Authentication auth) {
        return ResponseEntity.ok(shoeService.getShoes(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createShoe(
            @RequestBody Map<String, Object> request,
            Authentication auth) {
        return ResponseEntity.ok(shoeService.createShoe(request, auth.getName()));
    }

    @PostMapping("/{id}/retire")
    public ResponseEntity<Map<String, Object>> retireShoe(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(shoeService.retireShoe(id, auth.getName()));
    }
}
