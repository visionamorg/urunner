package com.runhub.running.repository;

import com.runhub.running.model.LiveTrackingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LiveTrackingRepository extends JpaRepository<LiveTrackingSession, Long> {
    Optional<LiveTrackingSession> findByToken(String token);
    Optional<LiveTrackingSession> findByUserIdAndActiveTrue(Long userId);
}
