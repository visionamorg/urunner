package com.runhub.coaching.repository;

import com.runhub.coaching.model.CoachingConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachingConnectionRepository extends JpaRepository<CoachingConnection, Long> {

    List<CoachingConnection> findByCoachIdAndStatus(Long coachId, String status);

    List<CoachingConnection> findByAthleteIdAndStatus(Long athleteId, String status);

    Optional<CoachingConnection> findByInviteToken(String token);

    Optional<CoachingConnection> findByCoachIdAndAthleteId(Long coachId, Long athleteId);

    boolean existsByCoachIdAndAthleteIdAndStatusNot(Long coachId, Long athleteId, String status);
}
