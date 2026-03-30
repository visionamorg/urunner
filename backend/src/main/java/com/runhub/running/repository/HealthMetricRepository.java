package com.runhub.running.repository;

import com.runhub.running.model.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {

    Optional<HealthMetric> findByUserIdAndDate(Long userId, LocalDate date);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);

    List<HealthMetric> findByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate from, LocalDate to);

    List<HealthMetric> findTop30ByUserIdOrderByDateDesc(Long userId);

    Optional<HealthMetric> findTopByUserIdOrderByDateDesc(Long userId);
}
