package com.runhub.running.service;

import com.runhub.coaching.repository.CoachingConnectionRepository;
import com.runhub.running.dto.BulkPushRequest;
import com.runhub.running.dto.BulkPushResultDto;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GarminClipboardService {

    private final GarminTrainingService garminTrainingService;
    private final UserRepository userRepository;
    private final CoachingConnectionRepository connectionRepository;

    public BulkPushResultDto bulkPushWorkout(User coach, BulkPushRequest request) {
        List<BulkPushResultDto.AthleteResult> results = new ArrayList<>();

        for (Long athleteId : request.getAthleteIds()) {
            BulkPushResultDto.AthleteResult result;

            // Verify ACTIVE coaching connection
            boolean hasConnection = connectionRepository
                    .findByCoachIdAndAthleteId(coach.getId(), athleteId)
                    .map(c -> "ACTIVE".equals(c.getStatus()))
                    .orElse(false);

            if (!hasConnection) {
                result = BulkPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId)
                        .username("unknown")
                        .success(false)
                        .error("No active coaching connection with athlete " + athleteId)
                        .build();
                results.add(result);
                continue;
            }

            User athlete = userRepository.findById(athleteId).orElse(null);
            if (athlete == null) {
                result = BulkPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId)
                        .username("unknown")
                        .success(false)
                        .error("Athlete not found")
                        .build();
                results.add(result);
                continue;
            }

            try {
                Map<String, Object> pushResult = garminTrainingService.pushWorkoutToGarmin(athlete, request.getSessionId());
                boolean success = Boolean.TRUE.equals(pushResult.get("success"));
                result = BulkPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId)
                        .username(athlete.getDisplayUsername())
                        .success(success)
                        .garminWorkoutId(success ? String.valueOf(pushResult.get("garminWorkoutId")) : null)
                        .error(success ? null : String.valueOf(pushResult.get("error")))
                        .build();
            } catch (Exception e) {
                log.error("Failed to push workout to athlete {} for coach {}", athleteId, coach.getId(), e);
                result = BulkPushResultDto.AthleteResult.builder()
                        .athleteId(athleteId)
                        .username(athlete.getDisplayUsername())
                        .success(false)
                        .error(e.getMessage())
                        .build();
            }

            results.add(result);
        }

        return BulkPushResultDto.builder()
                .sessionId(request.getSessionId())
                .sessionTitle("Session #" + request.getSessionId())
                .results(results)
                .build();
    }
}
