package com.runhub.events.repository;

import com.runhub.events.model.EventGalleryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventGalleryRepository extends JpaRepository<EventGalleryPhoto, Long> {
    List<EventGalleryPhoto> findByEventIdOrderByCreatedAtDesc(Long eventId);
    boolean existsByEventIdAndDriveFileId(Long eventId, String driveFileId);
    long countByEventId(Long eventId);
}
