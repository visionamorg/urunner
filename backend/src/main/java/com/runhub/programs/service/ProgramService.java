package com.runhub.programs.service;

import com.runhub.communities.model.Community;
import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.programs.dto.*;
import com.runhub.programs.mapper.ProgramMapper;
import com.runhub.programs.model.Program;
import com.runhub.programs.model.ProgramSession;
import com.runhub.programs.model.UserProgramProgress;
import com.runhub.programs.repository.ProgramRepository;
import com.runhub.programs.repository.ProgramSessionRepository;
import com.runhub.programs.repository.UserProgramProgressRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ProgramSessionRepository sessionRepository;
    private final UserProgramProgressRepository progressRepository;
    private final ProgramMapper programMapper;
    private final UserService userService;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

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

    // ── Community Programmes ─────────────────────────────────────────────────

    public List<ProgramDto> getCommunityPrograms(Long communityId) {
        return programRepository.findByCommunityIdOrderByCreatedAtDesc(communityId).stream()
                .map(this::enrichDto).toList();
    }

    @Transactional
    public ProgramDto createCommunityProgram(Long communityId, CreateProgramRequest req, User admin) {
        requireCommunityAdmin(communityId, admin);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        Program program = Program.builder()
                .name(req.getName())
                .description(req.getDescription())
                .level(req.getLevel() != null ? req.getLevel() : "BEGINNER")
                .durationWeeks(req.getDurationWeeks())
                .targetDistanceKm(req.getTargetDistanceKm())
                .community(community)
                .createdBy(admin)
                .sessions(new ArrayList<>())
                .build();
        program = programRepository.save(program);

        if (req.getSessions() != null) {
            for (ProgramSessionDto s : req.getSessions()) {
                ProgramSession session = ProgramSession.builder()
                        .program(program)
                        .weekNumber(s.getWeekNumber())
                        .dayNumber(s.getDayNumber())
                        .title(s.getTitle())
                        .description(s.getDescription())
                        .distanceKm(s.getDistanceKm())
                        .durationMinutes(s.getDurationMinutes())
                        .build();
                sessionRepository.save(session);
                program.getSessions().add(session);
            }
        }
        return enrichDto(program);
    }

    @Transactional
    public void deleteCommunityProgram(Long communityId, Long programId, User admin) {
        requireCommunityAdmin(communityId, admin);
        Program program = findById(programId);
        if (program.getCommunity() == null || !communityId.equals(program.getCommunity().getId()))
            throw new BadRequestException("Programme does not belong to this community");
        programRepository.delete(program);
    }

    @Transactional
    public ProgramSessionDto addSession(Long programId, ProgramSessionDto dto, User admin) {
        Program program = findById(programId);
        if (program.getCommunity() != null)
            requireCommunityAdmin(program.getCommunity().getId(), admin);
        ProgramSession session = ProgramSession.builder()
                .program(program)
                .weekNumber(dto.getWeekNumber())
                .dayNumber(dto.getDayNumber())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .distanceKm(dto.getDistanceKm())
                .durationMinutes(dto.getDurationMinutes())
                .build();
        session = sessionRepository.save(session);
        return programMapper.toSessionDto(session);
    }

    public List<EnrolleeProgressDto> getProgramEnrollees(Long programId) {
        int totalSessions = sessionRepository.findByProgramIdOrderByWeekNumberAscDayNumberAsc(programId).size();
        return progressRepository.findByProgramId(programId).stream().map(p -> {
            EnrolleeProgressDto dto = new EnrolleeProgressDto();
            dto.setUsername(p.getUser().getDisplayUsername());
            dto.setCompletedSessions(p.getCompletedSessions());
            dto.setTotalSessions(totalSessions);
            dto.setStatus(p.getStatus());
            return dto;
        }).toList();
    }

    @Transactional
    public ProgramProgressDto completeSession(Long programId, String email) {
        User user = userService.getUserEntityByEmail(email);
        UserProgramProgress progress = progressRepository.findByUserId(user.getId()).stream()
                .filter(p -> p.getProgram().getId().equals(programId) && "ACTIVE".equals(p.getStatus()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Not enrolled in this programme"));
        int total = sessionRepository.findByProgramIdOrderByWeekNumberAscDayNumberAsc(programId).size();
        progress.setCompletedSessions(Math.min(progress.getCompletedSessions() + 1, total));
        if (progress.getCompletedSessions() >= total) {
            progress.setStatus("COMPLETED");
        }
        progressRepository.save(progress);
        return buildProgressDto(progress);
    }

    private void requireCommunityAdmin(Long communityId, User user) {
        String role = communityMemberRepository.findByCommunityId(communityId).stream()
                .filter(m -> m.getUser().getId().equals(user.getId()))
                .map(CommunityMember::getRole)
                .findFirst().orElse(null);
        if (!"ADMIN".equals(role))
            throw new BadRequestException("Only community admins can manage programmes");
    }

    private ProgramDto enrichDto(Program p) {
        ProgramDto dto = programMapper.toDto(p);
        dto.setSessionsCount(sessionRepository.findByProgramIdOrderByWeekNumberAscDayNumberAsc(p.getId()).size());
        return dto;
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
