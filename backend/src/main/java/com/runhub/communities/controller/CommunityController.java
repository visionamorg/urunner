package com.runhub.communities.controller;

import com.runhub.communities.dto.CommunityDto;
import com.runhub.communities.dto.CommunityMemberDto;
import com.runhub.communities.dto.CreateCommunityRequest;
import com.runhub.communities.dto.UpdateCommunityRequest;
import com.runhub.communities.service.CommunityService;
import com.runhub.feed.dto.CreatePostRequest;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.service.FeedService;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<List<CommunityDto>> getAllCommunities(@AuthenticationPrincipal User user) {
        if (user != null) {
            return ResponseEntity.ok(communityService.getAllCommunities(user));
        }
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> getCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        if (user != null) {
            return ResponseEntity.ok(communityService.getCommunity(id, user));
        }
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @PostMapping
    public ResponseEntity<CommunityDto> createCommunity(
            @AuthenticationPrincipal User user,
            @RequestBody CreateCommunityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityService.createCommunity(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDto> updateCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody UpdateCommunityRequest request) {
        return ResponseEntity.ok(communityService.updateCommunity(id, request, user));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        communityService.joinCommunity(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        communityService.leaveCommunity(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<CommunityMemberDto>> getMembers(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getMembers(id));
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<Page<PostDto>> getCommunityFeed(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedService.getCommunityPosts(id, user, page));
    }

    @PostMapping("/{id}/feed")
    public ResponseEntity<PostDto> createCommunityPost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody CreatePostRequest request) {
        request.setCommunityId(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedService.createPost(request, user));
    }

    @PostMapping("/{id}/drive/sync")
    public ResponseEntity<PostDto> syncDrivePhotos(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(communityService.syncDrivePhotos(id, user));
    }
}
