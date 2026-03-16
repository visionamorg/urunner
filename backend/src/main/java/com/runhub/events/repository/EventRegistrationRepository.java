package com.runhub.events.repository;

import com.runhub.events.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    List<EventRegistration> findByEventId(Long eventId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT COUNT(r) FROM EventRegistration r WHERE r.event.id = :eventId AND r.status = 'REGISTERED'")
    long countActiveByEventId(@Param("eventId") Long eventId);
}
