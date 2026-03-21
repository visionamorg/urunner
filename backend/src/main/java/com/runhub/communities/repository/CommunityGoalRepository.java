package com.runhub.communities.repository;

import com.runhub.communities.model.CommunityGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityGoalRepository extends JpaRepository<CommunityGoal, Long> {
    Optional<CommunityGoal> findTopByCommunityIdOrderByCreatedAtDesc(Long communityId);
}
