package com.runhub.segments.repository;

import com.runhub.segments.model.SegmentEffort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentEffortRepository extends JpaRepository<SegmentEffort, Long> {
    List<SegmentEffort> findBySegmentIdOrderByElapsedSecondsAsc(Long segmentId, Pageable pageable);

    @Query("SELECT se FROM SegmentEffort se WHERE se.user.id = :userId ORDER BY se.elapsedSeconds ASC")
    List<SegmentEffort> findByUserIdOrderByElapsedSecondsAsc(Long userId);

    Optional<SegmentEffort> findTopBySegmentIdAndUserIdOrderByElapsedSecondsAsc(Long segmentId, Long userId);
}
