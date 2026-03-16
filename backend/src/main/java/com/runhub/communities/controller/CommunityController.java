package com.runhub.communities.controller;

import com.runhub.communities.dto.*;
import com.runhub.communities.service.CommunityService;
import com.runhub.feed.dto.CreatePostRequest;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.service.FeedService;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final FeedService feedService;

    // ── Community CRUD ────────────────────────────────────────────────────────

    @GetMapping
    public List<CommunityDto> getAll(@AuthenticationPrincipal User user) {
        return communityService.getAllCommunities(user);
    }

    @PostMapping
    public CommunityDto create(@RequestBody CreateCommunityRequest req,
                               @AuthenticationPrincipal User user) {
        return communityService.createCommunity(req, user);
    }

    @GetMapping("/{id}")
    public CommunityDto getOne(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return communityService.getCommunity(id, user);
    }

    @PutMapping("/{id}")
    public CommunityDto update(@PathVariable Long id,
                               @RequestBody UpdateCommunityRequest req,
                               @AuthenticationPrincipal User user) {
        return communityService.updateCommunity(id, req, user);
    }

    // ── Join / Leave ──────────────────────────────────────────────────────────

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> join(@PathVariable Long id, @AuthenticationPrincipal User user) {
        communityService.joinCommunity(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leave(@PathVariable Long id, @AuthenticationPrincipal User user) {
        communityService.leaveCommunity(id, user);
        return ResponseEntity.ok().build();
    }

    // ── Members ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}/members")
    public List<CommunityMemberDto> getMembers(@PathVariable Long id) {
        return communityService.getMembers(id);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> kickMember(@PathVariable Long id,
                                           @PathVariable Long userId,
                                           @AuthenticationPrincipal User user) {
        communityService.kickMember(id, userId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/members/{userId}/role")
    public ResponseEntity<Void> changeRole(@PathVariable Long id,
                                           @PathVariable Long userId,
                                           @RequestBody Map<String, String> body,
                                           @AuthenticationPrincipal User user) {
        communityService.changeMemberRole(id, userId, body.get("role"), user);
        return ResponseEntity.ok().build();
    }

    // ── Feed ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/feed")
    public Object getCommunityFeed(@PathVariable Long id,
                                   @RequestParam(defaultValue = "0") int page,
                                   @AuthenticationPrincipal User user) {
        return feedService.getCommunityPosts(id, user, page);
    }

    @PostMapping("/{id}/feed")
    public PostDto createPost(@PathVariable Long id,
                              @RequestBody CreatePostRequest req,
                              @AuthenticationPrincipal User user) {
        req.setCommunityId(id);
        return feedService.createPost(req, user);
    }

    @DeleteMapping("/{id}/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id,
                                           @PathVariable Long postId,
                                           @AuthenticationPrincipal User user) {
        communityService.deletePost(id, postId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/posts/{postId}/pin")
    public ResponseEntity<Void> pinPost(@PathVariable Long id,
                                        @PathVariable Long postId,
                                        @AuthenticationPrincipal User user) {
        communityService.pinPost(id, postId, user);
        return ResponseEntity.ok().build();
    }

    // ── Drive Sync ────────────────────────────────────────────────────────────

    @PostMapping("/{id}/drive/sync")
    public PostDto syncDrive(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return communityService.syncDrivePhotos(id, user);
    }

    // ── Invites ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}/invites")
    public InviteDto invite(@PathVariable Long id,
                            @RequestBody InviteRequest req,
                            @AuthenticationPrincipal User user) {
        return communityService.inviteUser(id, req.getUsername(), user);
    }

    @GetMapping("/{id}/invites")
    public List<InviteDto> getCommunityInvites(@PathVariable Long id,
                                               @AuthenticationPrincipal User user) {
        return communityService.getCommunityInvites(id, user);
    }

    @DeleteMapping("/{id}/invites/{inviteId}")
    public ResponseEntity<Void> cancelInvite(@PathVariable Long id,
                                             @PathVariable Long inviteId,
                                             @AuthenticationPrincipal User user) {
        communityService.cancelInvite(id, inviteId, user);
        return ResponseEntity.ok().build();
    }

    // ── My Invites (user-facing) ──────────────────────────────────────────────

    @GetMapping("/invites/mine")
    public List<InviteDto> getMyInvites(@AuthenticationPrincipal User user) {
        return communityService.getMyInvites(user);
    }

    @PostMapping("/invites/{token}/accept")
    public ResponseEntity<Void> acceptInvite(@PathVariable String token,
                                             @AuthenticationPrincipal User user) {
        communityService.respondToInvite(token, true, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invites/{token}/decline")
    public ResponseEntity<Void> declineInvite(@PathVariable String token,
                                              @AuthenticationPrincipal User user) {
        communityService.respondToInvite(token, false, user);
        return ResponseEntity.ok().build();
    }
}
