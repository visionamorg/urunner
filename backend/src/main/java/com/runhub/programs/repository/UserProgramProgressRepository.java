package com.runhub.programs.repository;

import com.runhub.programs.model.UserProgramProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProgramProgressRepository extends JpaRepository<UserProgramProgress, Long> {
    List<UserProgramProgress> findByUserId(Long userId);
    List<UserProgramProgress> findByUserIdAndStatus(Long userId, String status);
    boolean existsByUserIdAndProgramIdAndStatus(Long userId, Long programId, String status);
}
