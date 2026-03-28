package com.runhub.communities.repository;

import com.runhub.communities.model.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    List<ChallengeParticipant> findByChallengeIdOrderByContributionDesc(Long challengeId);
    Optional<ChallengeParticipant> findByChallengeIdAndUserId(Long challengeId, Long userId);
    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);

    @Query("SELECT cp.user.id FROM ChallengeParticipant cp WHERE cp.challenge.id = :challengeId")
    List<Long> findUserIdsByChallengeId(@Param("challengeId") Long challengeId);
}
