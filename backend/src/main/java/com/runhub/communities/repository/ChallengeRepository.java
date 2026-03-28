package com.runhub.communities.repository;

import com.runhub.communities.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByCommunityIdOrderByCreatedAtDesc(Long communityId);
    List<Challenge> findByCommunityIdAndStatus(Long communityId, String status);
}
