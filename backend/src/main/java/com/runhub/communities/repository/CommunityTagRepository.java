package com.runhub.communities.repository;

import com.runhub.communities.model.CommunityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityTagRepository extends JpaRepository<CommunityTag, Long> {
    List<CommunityTag> findByCommunityIdOrderByNameAsc(Long communityId);
}
