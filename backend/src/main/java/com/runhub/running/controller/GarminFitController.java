package com.runhub.running.controller;

import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.service.GarminFitService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/fit")
@RequiredArgsConstructor
public class GarminFitController {

    private final GarminFitService garminFitService;
    private final UserService userService;

    /**
     * Upload a .FIT file to import activities.
     * POST /api/fit/upload
     * Content-Type: multipart/form-data
     * Form field: file (the .FIT binary)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SyncResultDto> uploadFit(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(SyncResultDto.builder().message("No file provided").build());
        }

        String filename = file.getOriginalFilename();
        if (filename != null && !filename.toLowerCase().endsWith(".fit")) {
            return ResponseEntity.badRequest()
                    .body(SyncResultDto.builder().message("File must have a .fit extension").build());
        }

        User user = userService.getUserEntityByEmail(auth.getName());
        SyncResultDto result = garminFitService.importFitFile(user, file);
        return ResponseEntity.ok(result);
    }
}
