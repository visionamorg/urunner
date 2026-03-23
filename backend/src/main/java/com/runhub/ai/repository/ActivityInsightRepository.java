package com.runhub.ai.repository;

import com.runhub.ai.model.ActivityInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityInsightRepository extends JpaRepository<ActivityInsight, Long> {
    Optional<ActivityInsight> findByActivityId(Long activityId);
}
