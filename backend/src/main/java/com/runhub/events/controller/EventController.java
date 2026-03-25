package com.runhub.events.controller;

import com.runhub.events.dto.CreateEventRequest;
import com.runhub.events.dto.EventDto;
import com.runhub.events.dto.EventParticipantDto;
import com.runhub.events.dto.GalleryPhotoDto;
import com.runhub.events.service.EventService;
import com.runhub.events.service.EventGalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventGalleryService galleryService;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(Principal principal, @RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(principal.getName(), request));
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<Void> registerForEvent(@PathVariable Long id, Principal principal) {
        eventService.registerForEvent(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<EventParticipantDto>> getParticipants(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventParticipants(id));
    }

    // ── Gallery ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/gallery")
    public ResponseEntity<List<GalleryPhotoDto>> getGallery(@PathVariable Long id) {
        return ResponseEntity.ok(galleryService.getGalleryPhotos(id));
    }

    @PostMapping("/{id}/gallery/link-drive")
    public ResponseEntity<Void> linkDriveFolder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        galleryService.linkDriveFolder(id, body.get("folderId"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/gallery/sync")
    public ResponseEntity<Map<String, Integer>> syncGallery(@PathVariable Long id) {
        int imported = galleryService.syncDrivePhotos(id);
        return ResponseEntity.ok(Map.of("imported", imported));
    }

    @PostMapping("/{id}/gallery")
    public ResponseEntity<GalleryPhotoDto> addPhoto(@PathVariable Long id,
                                                      @RequestBody Map<String, String> body,
                                                      Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(galleryService.addPhoto(id, body.get("photoUrl"), principal.getName()));
    }
}
