package com.runhub.segments.controller;

import com.runhub.segments.dto.SegmentDto;
import com.runhub.segments.service.SegmentService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/segments")
@RequiredArgsConstructor
public class SegmentController {

    private final SegmentService segmentService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<SegmentDto>> getAllSegments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetails != null ? userService.getUserEntityByEmail(userDetails.getUsername()) : null;
        return ResponseEntity.ok(segmentService.getAllSegments(user));
    }

    @GetMapping("/my-efforts")
    public ResponseEntity<List<SegmentDto>> getMyEfforts(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(segmentService.getMyEfforts(user));
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<SegmentDto>> getLeaderboard(@PathVariable Long id) {
        return ResponseEntity.ok(segmentService.getLeaderboard(id));
    }
}
