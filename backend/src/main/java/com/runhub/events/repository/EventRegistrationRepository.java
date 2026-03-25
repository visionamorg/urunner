package com.runhub.events.repository;

import com.runhub.events.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    List<EventRegistration> findByEventId(Long eventId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT COUNT(r) FROM EventRegistration r WHERE r.event.id = :eventId AND (r.status = 'REGISTERED' OR r.status = 'CONFIRMED') AND r.role = 'RUNNER'")
    long countActiveByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(r) FROM EventRegistration r WHERE r.event.id = :eventId AND r.status = 'WAITLISTED' AND r.role = 'RUNNER'")
    long countWaitlistedByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(r) FROM EventRegistration r WHERE r.event.id = :eventId AND (r.status = 'REGISTERED' OR r.status = 'CONFIRMED') AND r.role = 'VOLUNTEER'")
    long countVolunteersByEventId(@Param("eventId") Long eventId);

    @Query("SELECT r FROM EventRegistration r WHERE r.event.id = :eventId AND r.status = 'WAITLISTED' AND r.role = 'RUNNER' ORDER BY r.registeredAt ASC")
    List<EventRegistration> findWaitlistedByEventId(@Param("eventId") Long eventId);

    List<EventRegistration> findByEventIdAndRole(Long eventId, String role);

    List<EventRegistration> findByEventIdAndStatus(Long eventId, String status);

    List<EventRegistration> findByEventIdAndRoleAndStatus(Long eventId, String role, String status);
}
