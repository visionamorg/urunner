package com.runhub.running.repository;

import com.runhub.running.model.GarminWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GarminWorkoutRepository extends JpaRepository<GarminWorkout, Long> {
    List<GarminWorkout> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    List<GarminWorkout> findByOwnerIdAndTemplateTrue(Long ownerId);
}
