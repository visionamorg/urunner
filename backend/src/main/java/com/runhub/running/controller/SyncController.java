package com.runhub.running.controller;

import com.runhub.config.ResourceNotFoundException;
import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.service.GarminSyncService;
import com.runhub.running.service.StravaSyncService;
import com.runhub.users.model.AuthProvider;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final StravaSyncService stravaSyncService;
    private final GarminSyncService garminSyncService;
    private final UserRepository userRepository;

    @PostMapping("/strava")
    public ResponseEntity<SyncResultDto> syncStrava(@AuthenticationPrincipal UserDetails principal) {
        User user = getUser(principal.getUsername());
        if (user.getAuthProvider() != AuthProvider.STRAVA || user.getProviderAccessToken() == null) {
            return ResponseEntity.badRequest().body(
                    SyncResultDto.builder().message("Strava account not connected").build());
        }
        return ResponseEntity.ok(stravaSyncService.syncActivities(user));
    }

    @PostMapping("/garmin")
    public ResponseEntity<SyncResultDto> syncGarmin(@AuthenticationPrincipal UserDetails principal) {
        User user = getUser(principal.getUsername());
        if (user.getAuthProvider() != AuthProvider.GARMIN || user.getProviderAccessToken() == null) {
            return ResponseEntity.badRequest().body(
                    SyncResultDto.builder().message("Garmin account not connected").build());
        }
        return ResponseEntity.ok(garminSyncService.syncActivities(user));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
