package com.runhub.programs.controller;

import com.runhub.programs.dto.ProgramDto;
import com.runhub.programs.dto.ProgramProgressDto;
import com.runhub.programs.dto.ProgramSessionDto;
import com.runhub.programs.service.ProgramService;
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

    @GetMapping
    public ResponseEntity<List<ProgramDto>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramDto> getProgramById(@PathVariable Long id) {
        return ResponseEntity.ok(programService.getProgramById(id));
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<ProgramSessionDto>> getProgramSessions(@PathVariable Long id) {
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
}
