package com.runhub.running.repository;

import com.runhub.running.model.TemplateVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TemplateVoteRepository extends JpaRepository<TemplateVote, Long> {
    Optional<TemplateVote> findByUserIdAndTemplateId(Long userId, Long templateId);
    boolean existsByUserIdAndTemplateId(Long userId, Long templateId);
}
