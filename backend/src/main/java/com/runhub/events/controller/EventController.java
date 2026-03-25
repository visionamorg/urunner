package com.runhub.events.controller;

import com.runhub.events.dto.CreateEventRequest;
import com.runhub.events.dto.EventDto;
import com.runhub.events.dto.EventParticipantDto;
import com.runhub.events.dto.GalleryPhotoDto;
import com.runhub.events.service.EventService;
import com.runhub.events.service.EventGalleryService;
import com.runhub.events.service.GpxService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventGalleryService galleryService;
    private final GpxService gpxService;

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
    public ResponseEntity<EventParticipantDto> registerForEvent(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(eventService.registerForEvent(id, principal.getName()));
    }

    @PostMapping("/{id}/register/volunteer")
    public ResponseEntity<EventParticipantDto> registerVolunteer(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(eventService.registerVolunteer(id, principal.getName()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long id, Principal principal) {
        eventService.cancelRegistration(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<EventParticipantDto>> getParticipants(
            @PathVariable Long id,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(eventService.getEventParticipants(id, role, status));
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

    // ── GPX Route ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}/gpx")
    public ResponseEntity<EventDto> uploadGpx(@PathVariable Long id,
                                               @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(gpxService.uploadGpx(id, file));
    }

    @GetMapping("/{id}/gpx/download")
    public ResponseEntity<Resource> downloadGpx(@PathVariable Long id) {
        try {
            Path path = gpxService.getGpxFile(id);
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"route.gpx\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/gpx")
    public ResponseEntity<Void> deleteGpx(@PathVariable Long id) {
        gpxService.deleteGpx(id);
        return ResponseEntity.ok().build();
    }
}
