package com.runhub.events.service;

import com.runhub.communities.service.GoogleDriveService;
import com.runhub.events.dto.GalleryPhotoDto;
import com.runhub.events.model.Event;
import com.runhub.events.model.EventGalleryPhoto;
import com.runhub.events.repository.EventGalleryRepository;
import com.runhub.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventGalleryService {

    private final EventRepository eventRepository;
    private final EventGalleryRepository galleryRepository;
    private final GoogleDriveService driveService;

    public List<GalleryPhotoDto> getGalleryPhotos(Long eventId) {
        return galleryRepository.findByEventIdOrderByCreatedAtDesc(eventId).stream()
                .map(this::toDto).toList();
    }

    @Transactional
    public void linkDriveFolder(Long eventId, String folderId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setDriveFolderId(folderId);
        eventRepository.save(event);
    }

    @Transactional
    public int syncDrivePhotos(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getDriveFolderId() == null) {
            throw new RuntimeException("No Drive folder linked to this event");
        }

        List<String> imageUrls = driveService.getImagesFromFolder(event.getDriveFolderId());
        int imported = 0;

        for (String url : imageUrls) {
            // Extract file ID from URL for dedup
            String fileId = extractFileId(url);
            if (fileId != null && galleryRepository.existsByEventIdAndDriveFileId(eventId, fileId)) {
                continue;
            }

            EventGalleryPhoto photo = EventGalleryPhoto.builder()
                    .event(event)
                    .photoUrl(url)
                    .thumbnailUrl(url.contains("?") ? url + "&sz=w400" : url + "?sz=w400")
                    .driveFileId(fileId)
                    .build();
            galleryRepository.save(photo);
            imported++;
        }

        return imported;
    }

    @Transactional
    public GalleryPhotoDto addPhoto(Long eventId, String photoUrl, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        EventGalleryPhoto photo = EventGalleryPhoto.builder()
                .event(event)
                .photoUrl(photoUrl)
                .thumbnailUrl(photoUrl)
                .uploadedByUsername(username)
                .build();
        photo = galleryRepository.save(photo);
        return toDto(photo);
    }

    private String extractFileId(String url) {
        // Google Drive URLs: https://drive.google.com/uc?id=FILE_ID&export=view
        if (url.contains("id=")) {
            int start = url.indexOf("id=") + 3;
            int end = url.indexOf("&", start);
            return end > 0 ? url.substring(start, end) : url.substring(start);
        }
        return null;
    }

    private GalleryPhotoDto toDto(EventGalleryPhoto photo) {
        return GalleryPhotoDto.builder()
                .id(photo.getId())
                .photoUrl(photo.getPhotoUrl())
                .thumbnailUrl(photo.getThumbnailUrl())
                .uploadedByUsername(photo.getUploadedByUsername())
                .bibNumber(photo.getBibNumber())
                .taggedUsername(photo.getTaggedUsername())
                .createdAt(photo.getCreatedAt())
                .build();
    }
}
