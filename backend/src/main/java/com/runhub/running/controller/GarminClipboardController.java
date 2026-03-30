package com.runhub.running.controller;

import com.runhub.running.dto.BulkPushRequest;
import com.runhub.running.dto.BulkPushResultDto;
import com.runhub.running.service.GarminClipboardService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/garmin/clipboard")
@RequiredArgsConstructor
public class GarminClipboardController {

    private final GarminClipboardService clipboardService;
    private final UserService userService;

    @PostMapping("/bulk-push")
    public ResponseEntity<BulkPushResultDto> bulkPush(
            @RequestBody BulkPushRequest request,
            Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(clipboardService.bulkPushWorkout(coach, request));
    }
}
