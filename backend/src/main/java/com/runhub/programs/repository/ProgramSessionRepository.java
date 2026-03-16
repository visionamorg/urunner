package com.runhub.programs.repository;

import com.runhub.programs.model.ProgramSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSessionRepository extends JpaRepository<ProgramSession, Long> {
    List<ProgramSession> findByProgramIdOrderByWeekNumberAscDayNumberAsc(Long programId);
}
