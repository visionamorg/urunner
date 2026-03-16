package com.runhub.feed.repository;

import com.runhub.feed.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByCommunityIdAndDeletedFalseOrderByPinnedDescCreatedAtDesc(Long communityId, Pageable pageable);
    Page<Post> findByCommunityIsNullAndDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}
