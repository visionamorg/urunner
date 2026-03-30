package com.runhub.running.controller;

import com.runhub.running.dto.*;
import com.runhub.running.service.GarminWorkoutService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/garmin/workouts")
@RequiredArgsConstructor
public class GarminWorkoutController {

    private final GarminWorkoutService workoutService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<GarminWorkoutDto>> list(Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(workoutService.listWorkouts(user));
    }

    @PostMapping
    public ResponseEntity<GarminWorkoutDto> create(@RequestBody CreateWorkoutRequest req, Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(workoutService.createWorkout(user, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarminWorkoutDto> update(@PathVariable Long id,
                                                    @RequestBody CreateWorkoutRequest req,
                                                    Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(workoutService.updateWorkout(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        workoutService.deleteWorkout(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/push-self")
    public ResponseEntity<Map<String, Object>> pushSelf(@PathVariable Long id,
                                                         @RequestBody PushSelfRequest req,
                                                         Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(workoutService.pushToSelf(user, id, req));
    }

    @PostMapping("/{id}/push-athletes")
    public ResponseEntity<WorkoutPushResultDto> pushAthletes(@PathVariable Long id,
                                                              @RequestBody PushAthletesRequest req,
                                                              Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(workoutService.pushToAthletes(coach, id, req));
    }

    @GetMapping("/athletes")
    public ResponseEntity<List<Map<String, Object>>> athletes(Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(workoutService.getAthletesWithGarminStatus(coach));
    }
}
