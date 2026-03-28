package com.runhub.users.repository;

import com.runhub.users.model.StealthZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StealthZoneRepository extends JpaRepository<StealthZone, Long> {
    List<StealthZone> findByUserIdOrderByCreatedAtDesc(Long userId);
}
