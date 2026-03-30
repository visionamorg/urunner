package com.runhub.coaching.repository;

import com.runhub.coaching.model.CoachingComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingCommentRepository extends JpaRepository<CoachingComment, Long> {

    List<CoachingComment> findByActivityIdOrderByCreatedAtAsc(Long activityId);

    List<CoachingComment> findByCoachIdOrderByCreatedAtDesc(Long coachId);

    List<CoachingComment> findByActivityIdAndLapNumberOrderByCreatedAtAsc(Long activityId, Integer lapNumber);
}
