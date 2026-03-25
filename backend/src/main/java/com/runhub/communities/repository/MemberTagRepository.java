package com.runhub.communities.repository;

import com.runhub.communities.model.MemberTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
    List<MemberTag> findByCommunityIdAndUserId(Long communityId, Long userId);
    List<MemberTag> findByCommunityId(Long communityId);
    void deleteByCommunityIdAndUserIdAndTagId(Long communityId, Long userId, Long tagId);
    boolean existsByCommunityIdAndUserIdAndTagId(Long communityId, Long userId, Long tagId);
}
