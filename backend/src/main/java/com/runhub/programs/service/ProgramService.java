package com.runhub.programs.service;

import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.programs.dto.*;
import com.runhub.programs.mapper.ProgramMapper;
import com.runhub.programs.model.Program;
import com.runhub.programs.model.UserProgramProgress;
import com.runhub.programs.repository.ProgramRepository;
import com.runhub.programs.repository.ProgramSessionRepository;
import com.runhub.programs.repository.UserProgramProgressRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ProgramSessionRepository sessionRepository;
    private final UserProgramProgressRepository progressRepository;
    private final ProgramMapper programMapper;
    private final UserService userService;

    public List<ProgramDto> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(p -> {
                    ProgramDto dto = programMapper.toDto(p);
                    dto.setSessionsCount(sessionRepository.findByProgramIdOrderByWeekNumberAscDayNumberAsc(p.getId()).size());
                    return dto;
                }).toList();
    }

    public ProgramDto getProgramById(Long id) {
        Program program = findById(id);
        ProgramDto dto = programMapper.toDto(program);
        dto.setSessionsCount(sessionRepository.findByProgramIdOrderByWeekNumberAscDayNumberAsc(id).size());
        return dto;
    }

    public List<ProgramSessionDto> getProgramSessions(Long programId) {
        findById(programId);
        return sessionRepository.findByProgramIdOrderByWeekNumberAscDayNumberAsc(programId)
                .stream().map(programMapper::toSessionDto).toList();
    }

    @Transactional
    public ProgramProgressDto startProgram(Long programId, String email) {
        User user = userService.getUserEntityByEmail(email);
        Program program = findById(programId);

        if (progressRepository.existsByUserIdAndProgramIdAndStatus(user.getId(), programId, "ACTIVE")) {
            throw new BadRequestException("You are already enrolled in this program");
        }

        UserProgramProgress progress = UserProgramProgress.builder()
                .user(user)
                .program(program)
                .status("ACTIVE")
                .build();

        UserProgramProgress saved = progressRepository.save(progress);
        return buildProgressDto(saved);
    }

    public List<ProgramProgressDto> getMyProgress(String email) {
        User user = userService.getUserEntityByEmail(email);
        return progressRepository.findByUserId(user.getId())
                .stream().map(this::buildProgressDto).toList();
    }

    private ProgramProgressDto buildProgressDto(UserProgramProgress p) {
        ProgramProgressDto dto = new ProgramProgressDto();
        dto.setId(p.getId());
        dto.setProgramId(p.getProgram().getId());
        dto.setProgramName(p.getProgram().getName());
        dto.setProgramLevel(p.getProgram().getLevel());
        dto.setDurationWeeks(p.getProgram().getDurationWeeks());
        dto.setStartedAt(p.getStartedAt());
        dto.setCompletedSessions(p.getCompletedSessions());
        dto.setTotalSessions(sessionRepository.findByProgramIdOrderByWeekNumberAscDayNumberAsc(p.getProgram().getId()).size());
        dto.setStatus(p.getStatus());
        return dto;
    }

    private Program findById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));
    }
}
