package com.runhub.feed.repository;

import com.runhub.feed.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.deleted = false ORDER BY p.createdAt DESC")
    Page<Post> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.community IS NULL AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<Post> findByCommunityIsNullAndDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.community.id = :communityId AND p.deleted = false ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Post> findCommunityFeed(@Param("communityId") Long communityId, Pageable pageable);
}
