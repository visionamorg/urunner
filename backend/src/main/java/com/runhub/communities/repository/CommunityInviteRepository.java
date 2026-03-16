package com.runhub.communities.repository;

import com.runhub.communities.model.CommunityInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityInviteRepository extends JpaRepository<CommunityInvite, Long> {
    List<CommunityInvite> findByCommunityIdAndStatus(Long communityId, String status);
    List<CommunityInvite> findByInvitedUserIdAndStatus(Long userId, String status);
    Optional<CommunityInvite> findByToken(String token);
    boolean existsByCommunityIdAndInvitedUserIdAndStatus(Long communityId, Long userId, String status);
}
