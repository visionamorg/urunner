package com.runhub.users.controller;

import com.runhub.users.dto.NotificationPreferenceDto;
import com.runhub.users.dto.PublicProfileDto;
import com.runhub.users.dto.UpdateUserRequest;
import com.runhub.users.dto.UserDto;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(Principal principal, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(principal.getName(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me/notification-preferences")
    public ResponseEntity<NotificationPreferenceDto> getNotificationPreferences(Principal principal) {
        return ResponseEntity.ok(userService.getNotificationPreferences(principal.getName()));
    }

    @PatchMapping("/me/notification-preferences")
    public ResponseEntity<NotificationPreferenceDto> updateNotificationPreferences(
            Principal principal,
            @RequestBody NotificationPreferenceDto request) {
        return ResponseEntity.ok(userService.updateNotificationPreferences(principal.getName(), request));
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<PublicProfileDto> getPublicProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(userService.getPublicProfile(username, email));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> follow(@PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.follow(userDetails.getUsername(), username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<Void> unfollow(@PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.unfollow(userDetails.getUsername(), username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String q) {
        return ResponseEntity.ok(userService.searchUsers(q));
    }
}
