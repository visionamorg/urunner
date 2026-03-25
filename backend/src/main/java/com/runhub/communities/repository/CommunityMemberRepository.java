package com.runhub.communities.repository;

import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.model.CommunityMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, CommunityMemberId> {

    List<CommunityMember> findByCommunityId(Long communityId);

    boolean existsByIdCommunityIdAndIdUserId(Long communityId, Long userId);

    @Query("SELECT cm.community.id FROM CommunityMember cm WHERE cm.user.id = :userId")
    List<Long> findCommunityIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT cm FROM CommunityMember cm WHERE cm.community.id = :communityId AND cm.user.id = :userId")
    Optional<CommunityMember> findByCommunityIdAndUserId(@Param("communityId") Long communityId, @Param("userId") Long userId);
}
