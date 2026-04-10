package com.runhub.users.repository;

import com.runhub.users.model.UserFollow;
import com.runhub.users.model.UserFollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollowId> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    long countByFollowingId(Long followingId);
    long countByFollowerId(Long followerId);
    List<UserFollow> findByFollowerId(Long followerId);
    List<UserFollow> findByFollowingId(Long followingId);
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Query("SELECT uf.followingId FROM UserFollow uf WHERE uf.followerId = :followerId")
    List<Long> findFollowingIdsByFollowerId(Long followerId);
}
