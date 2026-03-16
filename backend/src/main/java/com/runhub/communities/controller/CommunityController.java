package com.runhub.communities.controller;

import com.runhub.communities.dto.CommunityDto;
import com.runhub.communities.dto.CommunityMemberDto;
import com.runhub.communities.dto.CreateCommunityRequest;
import com.runhub.communities.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    public ResponseEntity<List<CommunityDto>> getAllCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> getCommunityById(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @PostMapping
    public ResponseEntity<CommunityDto> createCommunity(Principal principal, @RequestBody CreateCommunityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communityService.createCommunity(principal.getName(), request));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinCommunity(@PathVariable Long id, Principal principal) {
        communityService.joinCommunity(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<CommunityMemberDto>> getCommunityMembers(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getCommunityMembers(id));
    }
}
