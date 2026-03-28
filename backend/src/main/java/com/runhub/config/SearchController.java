package com.runhub.config;

import com.runhub.communities.model.Community;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.events.model.Event;
import com.runhub.events.repository.EventRepository;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String q,
            @RequestParam(required = false) String category) {

        String query = q.trim().toLowerCase();
        if (query.length() < 2) {
            return ResponseEntity.ok(Map.of("users", List.of(), "communities", List.of(), "events", List.of()));
        }

        Map<String, Object> results = new HashMap<>();

        if (category == null || "users".equals(category)) {
            List<Map<String, Object>> users = userRepository.findAll().stream()
                    .filter(u -> matchesQuery(u.getUsername(), query)
                            || matchesQuery(u.getFirstName(), query)
                            || matchesQuery(u.getLastName(), query))
                    .limit(10)
                    .map(u -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("type", "user");
                        m.put("id", u.getId());
                        m.put("title", u.getDisplayUsername());
                        m.put("subtitle", (u.getFirstName() != null ? u.getFirstName() + " " : "") + (u.getLastName() != null ? u.getLastName() : ""));
                        m.put("imageUrl", u.getProfileImageUrl());
                        return m;
                    }).toList();
            results.put("users", users);
        }

        if (category == null || "communities".equals(category)) {
            List<Map<String, Object>> communities = communityRepository.findAll().stream()
                    .filter(c -> matchesQuery(c.getName(), query)
                            || matchesQuery(c.getDescription(), query))
                    .limit(10)
                    .map(c -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("type", "community");
                        m.put("id", c.getId());
                        m.put("title", c.getName());
                        m.put("subtitle", c.getMemberCount() + " members");
                        m.put("imageUrl", c.getImageUrl());
                        return m;
                    }).toList();
            results.put("communities", communities);
        }

        if (category == null || "events".equals(category)) {
            List<Map<String, Object>> events = eventRepository.findAll().stream()
                    .filter(e -> matchesQuery(e.getName(), query)
                            || matchesQuery(e.getDescription(), query)
                            || matchesQuery(e.getLocation(), query))
                    .limit(10)
                    .map(e -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("type", "event");
                        m.put("id", e.getId());
                        m.put("title", e.getName());
                        m.put("subtitle", e.getLocation() != null ? e.getLocation() : "");
                        m.put("imageUrl", null);
                        return m;
                    }).toList();
            results.put("events", events);
        }

        return ResponseEntity.ok(results);
    }

    private boolean matchesQuery(String field, String query) {
        return field != null && field.toLowerCase().contains(query);
    }
}
