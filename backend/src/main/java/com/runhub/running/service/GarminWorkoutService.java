package com.runhub.running.service;

import com.runhub.auth.service.GarminOAuthService;
import com.runhub.coaching.repository.CoachingConnectionRepository;
import com.runhub.running.dto.*;
import com.runhub.running.model.GarminWorkout;
import com.runhub.running.repository.GarminWorkoutRepository;
import com.runhub.users.model.AuthProvider;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GarminWorkoutService {

    private final GarminWorkoutRepository workoutRepository;
    private final GarminTrainingService garminTrainingService;
    private final CoachingConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final GarminOAuthService garminOAuthService;

    // ── CRUD ────────────────────────────────────────────────────────────────

    public List<GarminWorkoutDto> listWorkouts(User user) {
        return workoutRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public GarminWorkoutDto createWorkout(User user, CreateWorkoutRequest req) {
        GarminWorkout workout = GarminWorkout.builder()
                .owner(user)
                .title(req.getTitle())
                .sport(req.getSport() != null ? req.getSport() : "RUNNING")
                .description(req.getDescription())
                .steps(req.getSteps() != null ? req.getSteps() : new ArrayList<>())
                .template(req.isTemplate())
                .build();
        return toDto(workoutRepository.save(workout));
    }

    @Transactional
    public GarminWorkoutDto updateWorkout(User user, Long id, CreateWorkoutRequest req) {
        GarminWorkout workout = getOwned(user, id);
        workout.setTitle(req.getTitle());
        if (req.getSport() != null) workout.setSport(req.getSport());
        workout.setDescription(req.getDescription());
        if (req.getSteps() != null) workout.setSteps(req.getSteps());
        workout.setTemplate(req.isTemplate());
        return toDto(workoutRepository.save(workout));
    }

    @Transactional
    public void deleteWorkout(User user, Long id) {
        GarminWorkout workout = getOwned(user, id);
        workoutRepository.delete(workout);
    }

    // ── Push to self ─────────────────────────────────────────────────────────

    public Map<String, Object> pushToSelf(User user, Long workoutId, PushSelfRequest req) {
        if (!isGarminConnected(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Garmin not connected. Please link your Garmin account first.");
        }
        GarminWorkout workout = getOwned(user, workoutId);
        return garminTrainingService.pushStructuredWorkout(user, workout, req.getScheduledDate());
    }

    // ── Push to athletes (coach) ──────────────────────────────────────────────

    public WorkoutPushResultDto pushToAthletes(User coach, Long workoutId, PushAthletesRequest req) {
        GarminWorkout workout = getOwned(coach, workoutId);

        // Resolve target athlete list
        List<Long> athleteIds = (req.getAthleteIds() != null && !req.getAthleteIds().isEmpty())
                ? req.getAthleteIds()
                : connectionRepository.findByCoachIdAndStatus(coach.getId(), "ACTIVE")
                        .stream().map(c -> c.getAthlete().getId()).toList();

        List<WorkoutPushResultDto.AthleteResult> results = new ArrayList<>();

        for (Long athleteId : athleteIds) {
            WorkoutPushResultDto.AthleteResult result;

            boolean hasConnection = connectionRepository
                    .findByCoachIdAndAthleteId(coach.getId(), athleteId)
                    .map(c -> "ACTIVE".equals(c.getStatus()))
                    .orElse(false);

            if (!hasConnection) {
                results.add(WorkoutPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId).username("unknown").success(false)
                        .error("No active coaching connection").build());
                continue;
            }

            User athlete = userRepository.findById(athleteId).orElse(null);
            if (athlete == null) {
                results.add(WorkoutPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId).username("unknown").success(false)
                        .error("Athlete not found").build());
                continue;
            }

            if (!isGarminConnected(athlete)) {
                results.add(WorkoutPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId).username(athlete.getDisplayUsername())
                        .success(false).error("Garmin not connected").build());
                continue;
            }

            try {
                Map<String, Object> pushResult = garminTrainingService.pushStructuredWorkout(
                        athlete, workout, req.getScheduledDate());
                boolean success = Boolean.TRUE.equals(pushResult.get("success"));
                results.add(WorkoutPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId).username(athlete.getDisplayUsername())
                        .success(success)
                        .garminWorkoutId(success ? String.valueOf(pushResult.get("garminWorkoutId")) : null)
                        .error(success ? null : String.valueOf(pushResult.get("error")))
                        .build());
            } catch (Exception e) {
                log.error("Failed to push workout {} to athlete {}", workoutId, athleteId, e);
                results.add(WorkoutPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId).username(athlete.getDisplayUsername())
                        .success(false).error(e.getMessage()).build());
            }
        }

        return WorkoutPushResultDto.builder()
                .workoutId(workout.getId())
                .workoutTitle(workout.getTitle())
                .results(results)
                .build();
    }

    // ── Athletes with Garmin status (for push dialog) ─────────────────────────

    public List<Map<String, Object>> getAthletesWithGarminStatus(User coach) {
        return connectionRepository.findByCoachIdAndStatus(coach.getId(), "ACTIVE")
                .stream().map(c -> {
                    User athlete = c.getAthlete();
                    return Map.<String, Object>of(
                            "athleteId", athlete.getId(),
                            "username", athlete.getDisplayUsername(),
                            "profileImageUrl", athlete.getProfileImageUrl() != null ? athlete.getProfileImageUrl() : "",
                            "garminConnected", isGarminConnected(athlete)
                    );
                }).toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private GarminWorkout getOwned(User user, Long id) {
        GarminWorkout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));
        if (!workout.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return workout;
    }

    private boolean isGarminConnected(User user) {
        return user.getAuthProvider() == AuthProvider.GARMIN
                && user.getProviderAccessToken() != null
                && !user.getProviderAccessToken().isBlank();
    }

    private GarminWorkoutDto toDto(GarminWorkout w) {
        return GarminWorkoutDto.builder()
                .id(w.getId())
                .title(w.getTitle())
                .sport(w.getSport())
                .description(w.getDescription())
                .steps(w.getSteps())
                .template(w.isTemplate())
                .stepCount(w.getSteps() != null ? w.getSteps().size() : 0)
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }
}
