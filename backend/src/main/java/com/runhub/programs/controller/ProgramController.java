package com.runhub.programs.controller;

import com.runhub.programs.dto.*;
import com.runhub.programs.service.ProgramService;
import com.runhub.programs.service.TrainingPlanAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;
    private final TrainingPlanAIService trainingPlanAIService;

    @GetMapping
    public ResponseEntity<List<ProgramDto>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramDto> getProgramById(@PathVariable Long id) {
        return ResponseEntity.ok(programService.getProgramById(id));
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<ProgramSessionDto>> getProgramSessions(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok(programService.getProgramSessionsGuarded(id, principal.getName()));
        }
        return ResponseEntity.ok(programService.getProgramSessions(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ProgramProgressDto> startProgram(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(programService.startProgram(id, principal.getName()));
    }

    @GetMapping("/my-progress")
    public ResponseEntity<List<ProgramProgressDto>> getMyProgress(Principal principal) {
        return ResponseEntity.ok(programService.getMyProgress(principal.getName()));
    }

    @PostMapping("/generate")
    public ResponseEntity<ProgramDto> generatePlan(@RequestBody GeneratePlanRequest request, Principal principal) {
        return ResponseEntity.ok(trainingPlanAIService.generatePlan(request, principal.getName()));
    }

    @GetMapping("/{id}/today-session")
    public ResponseEntity<ProgramSessionDto> getTodaySession(@PathVariable Long id, Principal principal) {
        return programService.getTodaySession(id, principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/{id}/complete-session")
    public ResponseEntity<ProgramProgressDto> completeSession(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(programService.completeSession(id, principal.getName()));
    }
}
