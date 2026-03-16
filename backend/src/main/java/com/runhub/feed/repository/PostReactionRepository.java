package com.runhub.feed.repository;

import com.runhub.feed.model.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    List<PostReaction> findByPostId(Long postId);

    Optional<PostReaction> findByPostIdAndUserId(Long postId, Long userId);

    @Query("SELECT r.emoji, COUNT(r) FROM PostReaction r WHERE r.post.id = :postId GROUP BY r.emoji")
    List<Object[]> countByEmojiForPost(@Param("postId") Long postId);
}
