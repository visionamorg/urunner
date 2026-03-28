package com.runhub.users.controller;

import com.runhub.users.model.StealthZone;
import com.runhub.users.model.User;
import com.runhub.users.repository.StealthZoneRepository;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/stealth-zones")
@RequiredArgsConstructor
public class StealthZoneController {

    private final StealthZoneRepository stealthZoneRepository;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getZones(Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(stealthZoneRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toMap).toList());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createZone(
            @RequestBody Map<String, Object> req,
            Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        StealthZone zone = StealthZone.builder()
                .user(user)
                .label((String) req.get("label"))
                .latitude(Double.parseDouble(req.get("latitude").toString()))
                .longitude(Double.parseDouble(req.get("longitude").toString()))
                .radiusMeters(Integer.parseInt(req.getOrDefault("radiusMeters", "500").toString()))
                .build();
        return ResponseEntity.ok(toMap(stealthZoneRepository.save(zone)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id, Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        stealthZoneRepository.findById(id).ifPresent(z -> {
            if (z.getUser().getId().equals(user.getId())) {
                stealthZoneRepository.delete(z);
            }
        });
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> toMap(StealthZone z) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", z.getId());
        m.put("label", z.getLabel());
        m.put("latitude", z.getLatitude());
        m.put("longitude", z.getLongitude());
        m.put("radiusMeters", z.getRadiusMeters());
        return m;
    }
}
