package com.runhub.communities.repository;

import com.runhub.communities.model.CommunitySponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunitySponsorRepository extends JpaRepository<CommunitySponsor, Long> {
    List<CommunitySponsor> findByCommunityId(Long communityId);
    long countByCommunityId(Long communityId);
    void deleteByCommunityIdAndId(Long communityId, Long id);
}
