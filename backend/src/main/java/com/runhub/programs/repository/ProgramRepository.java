package com.runhub.programs.repository;

import com.runhub.programs.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findByCommunityIdOrderByCreatedAtDesc(Long communityId);
}
