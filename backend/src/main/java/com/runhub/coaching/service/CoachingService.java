package com.runhub.coaching.service;

import com.runhub.coaching.dto.CoachingConnectionDto;
import com.runhub.coaching.model.CoachingConnection;
import com.runhub.coaching.repository.CoachingConnectionRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachingService {

    private final CoachingConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    @Transactional
    public CoachingConnectionDto inviteAthlete(User coach, String usernameOrEmail, String accessLevel) {
        // Find athlete by username, fall back to email
        User athlete = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + usernameOrEmail)));

        if (athlete.getId().equals(coach.getId())) {
            throw new BadRequestException("Cannot invite yourself as an athlete");
        }

        // Check for existing PENDING or ACTIVE connection
        boolean exists = connectionRepository.existsByCoachIdAndAthleteIdAndStatusNot(
                coach.getId(), athlete.getId(), "REVOKED");
        if (exists) {
            throw new BadRequestException("A coaching connection already exists with this athlete");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        CoachingConnection connection = CoachingConnection.builder()
                .coach(coach)
                .athlete(athlete)
                .garminAccessLevel(accessLevel != null ? accessLevel : "BASIC")
                .status("PENDING")
                .inviteToken(token)
                .build();

        return toDto(connectionRepository.save(connection));
    }

    @Transactional
    public CoachingConnectionDto acceptInvite(User athlete, String token) {
        CoachingConnection connection = connectionRepository.findByInviteToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invite not found or already used"));

        if (!connection.getAthlete().getId().equals(athlete.getId())) {
            throw new BadRequestException("This invite is not for you");
        }

        if (!"PENDING".equals(connection.getStatus())) {
            throw new BadRequestException("Invite is no longer pending");
        }

        connection.setStatus("ACTIVE");
        connection.setInviteToken(null);
        return toDto(connectionRepository.save(connection));
    }

    @Transactional
    public void revokeConnection(User user, Long connectionId) {
        CoachingConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));

        boolean isCoach = connection.getCoach().getId().equals(user.getId());
        boolean isAthlete = connection.getAthlete().getId().equals(user.getId());
        if (!isCoach && !isAthlete) {
            throw new BadRequestException("Not authorized to revoke this connection");
        }

        connection.setStatus("REVOKED");
        connectionRepository.save(connection);
    }

    public List<CoachingConnectionDto> getMyAthletes(User coach) {
        return connectionRepository.findByCoachIdAndStatus(coach.getId(), "ACTIVE")
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CoachingConnectionDto> getMyCoaches(User athlete) {
        return connectionRepository.findByAthleteIdAndStatus(athlete.getId(), "ACTIVE")
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CoachingConnectionDto> getPendingInvites(User athlete) {
        return connectionRepository.findByAthleteIdAndStatus(athlete.getId(), "PENDING")
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private CoachingConnectionDto toDto(CoachingConnection c) {
        return CoachingConnectionDto.builder()
                .id(c.getId())
                .coachId(c.getCoach().getId())
                .coachUsername(c.getCoach().getDisplayUsername())
                .athleteId(c.getAthlete().getId())
                .athleteUsername(c.getAthlete().getDisplayUsername())
                .athleteProfileImageUrl(c.getAthlete().getProfileImageUrl())
                .garminAccessLevel(c.getGarminAccessLevel())
                .status(c.getStatus())
                .inviteToken(c.getInviteToken())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
