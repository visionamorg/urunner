package com.runhub.communities.repository;

import com.runhub.communities.model.CommunityJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityJoinRequestRepository extends JpaRepository<CommunityJoinRequest, Long> {
    List<CommunityJoinRequest> findByCommunityIdAndStatus(Long communityId, String status);
    Optional<CommunityJoinRequest> findByCommunityIdAndUserId(Long communityId, Long userId);
    boolean existsByCommunityIdAndUserIdAndStatus(Long communityId, Long userId, String status);
}
