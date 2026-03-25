package com.runhub.notifications.controller;

import com.runhub.notifications.dto.NotificationDto;
import com.runhub.notifications.service.NotificationService;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public List<NotificationDto> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserEntityByEmail(userDetails.getUsername()).getId();
        return notificationService.getUserNotifications(userId);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserEntityByEmail(userDetails.getUsername()).getId();
        return Map.of("count", notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserEntityByEmail(userDetails.getUsername()).getId();
        notificationService.markRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserEntityByEmail(userDetails.getUsername()).getId();
        notificationService.markAllRead(userId);
        return ResponseEntity.ok().build();
    }
}
