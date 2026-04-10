package com.runhub.segments.repository;

import com.runhub.segments.model.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {
    List<Segment> findAllByOrderByDistanceKmAsc();
}
