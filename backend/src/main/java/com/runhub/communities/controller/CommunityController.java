package com.runhub.communities.controller;

import com.runhub.communities.dto.*;
import com.runhub.communities.dto.DriveFolderDto;
import com.runhub.communities.dto.JoinRequestDto;
import com.runhub.communities.service.CommunityService;
import com.runhub.communities.service.CommunityGoalService;
import com.runhub.events.dto.CreateEventRequest;
import com.runhub.events.dto.EventDto;
import com.runhub.events.dto.UpdateEventRequest;
import com.runhub.events.service.EventService;
import com.runhub.feed.dto.CreatePostRequest;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.service.FeedService;
import com.runhub.programs.dto.*;
import com.runhub.programs.service.ProgramService;
import com.runhub.users.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
@Tag(name = "Communities", description = "Create and manage running communities, members, feed, events, and programs")
public class CommunityController {

    private final CommunityService communityService;
    private final FeedService feedService;
    private final EventService eventService;
    private final CommunityGoalService goalService;
    private final ProgramService programService;
    private final com.runhub.communities.service.WeeklyDigestService weeklyDigestService;

    // ── Community CRUD ────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List communities", description = "Returns all communities; private communities are hidden unless the user is a member")
    public List<CommunityDto> getAll(@AuthenticationPrincipal User user) {
        return communityService.getAllCommunities(user);
    }

    @PostMapping
    @Operation(summary = "Create a community", description = "Creates a new running community with the authenticated user as admin")
    public CommunityDto create(@RequestBody CreateCommunityRequest req,
                               @AuthenticationPrincipal User user) {
        return communityService.createCommunity(req, user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get community", description = "Returns full details for a single community including member count and cover photo")
    public CommunityDto getOne(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return communityService.getCommunity(id, user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update community", description = "Updates community settings; requires ADMIN role within the community")
    public CommunityDto update(@PathVariable Long id,
                               @RequestBody UpdateCommunityRequest req,
                               @AuthenticationPrincipal User user) {
        return communityService.updateCommunity(id, req, user);
    }

    // ── Join / Leave ──────────────────────────────────────────────────────────

    @PostMapping("/{id}/join")
    @Operation(summary = "Join community", description = "Adds the authenticated user as a MEMBER of the community")
    public ResponseEntity<Void> join(@PathVariable Long id, @AuthenticationPrincipal User user) {
        communityService.joinCommunity(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/leave")
    @Operation(summary = "Leave community", description = "Removes the authenticated user from the community")
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

    // ── Tags ────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/tags")
    public List<CommunityTagDto> getTags(@PathVariable Long id) {
        return communityService.getCommunityTags(id);
    }

    @PostMapping("/{id}/tags")
    public CommunityTagDto createTag(@PathVariable Long id,
                                      @RequestBody Map<String, String> body,
                                      @AuthenticationPrincipal User user) {
        return communityService.createTag(id, body.get("name"), body.get("color"), user);
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id,
                                           @PathVariable Long tagId,
                                           @AuthenticationPrincipal User user) {
        communityService.deleteTag(id, tagId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/members/{userId}/tags/{tagId}")
    public ResponseEntity<Void> assignTag(@PathVariable Long id,
                                           @PathVariable Long userId,
                                           @PathVariable Long tagId,
                                           @AuthenticationPrincipal User user) {
        communityService.assignTag(id, userId, tagId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/members/{userId}/tags/{tagId}")
    public ResponseEntity<Void> removeTag(@PathVariable Long id,
                                           @PathVariable Long userId,
                                           @PathVariable Long tagId,
                                           @AuthenticationPrincipal User user) {
        communityService.removeTag(id, userId, tagId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/members/batch-notify")
    public ResponseEntity<Void> batchNotify(@PathVariable Long id,
                                             @RequestBody Map<String, List<Long>> body,
                                             @AuthenticationPrincipal User user) {
        communityService.batchNotifyInactive(id, body.get("userIds"), user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/members/batch-kick")
    public ResponseEntity<Void> batchKick(@PathVariable Long id,
                                           @RequestBody Map<String, List<Long>> body,
                                           @AuthenticationPrincipal User user) {
        communityService.batchKickMembers(id, body.get("userIds"), user);
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

    @GetMapping("/{id}/drive/folders")
    public List<DriveFolderDto> getDriveFolders(@PathVariable Long id,
                                                @AuthenticationPrincipal User user) {
        return communityService.getDriveFolders(id, user);
    }

    @PostMapping("/{id}/drive/sync")
    public PostDto syncDrive(@PathVariable Long id,
                             @RequestBody(required = false) Map<String, String> body,
                             @AuthenticationPrincipal User user) {
        if (body != null && body.containsKey("folderId")) {
            return communityService.syncDrivePhotos(id, body.get("folderId"), body.get("folderName"), user);
        }
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

    // ── Community Events ──────────────────────────────────────────────────────

    @GetMapping("/{id}/events")
    public List<EventDto> getCommunityEvents(@PathVariable Long id) {
        return eventService.getCommunityEvents(id);
    }

    @PostMapping("/{id}/events")
    public ResponseEntity<EventDto> createCommunityEvent(@PathVariable Long id,
                                                          @RequestBody CreateEventRequest request,
                                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createCommunityEvent(id, user.getEmail(), request));
    }

    @PutMapping("/{id}/events/{eid}")
    public EventDto updateCommunityEvent(@PathVariable Long id,
                                          @PathVariable Long eid,
                                          @RequestBody UpdateEventRequest request,
                                          @AuthenticationPrincipal User user) {
        return eventService.updateCommunityEvent(id, eid, request, user.getEmail());
    }

    @DeleteMapping("/{id}/events/{eid}")
    public ResponseEntity<Void> cancelCommunityEvent(@PathVariable Long id,
                                                      @PathVariable Long eid,
                                                      @AuthenticationPrincipal User user) {
        eventService.cancelCommunityEvent(id, eid, user.getEmail());
        return ResponseEntity.ok().build();
    }

    // ── Community Goal ────────────────────────────────────────────────────────

    @GetMapping("/{id}/goal")
    public ResponseEntity<?> getGoal(@PathVariable Long id) {
        return goalService.getCurrentGoal(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/{id}/goal")
    public ResponseEntity<CommunityGoalDto> setGoal(@PathVariable Long id,
                                                     @RequestBody CreateGoalRequest req,
                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.createGoal(id, req, user));
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

    // ── Community Programmes ──────────────────────────────────────────────────

    @GetMapping("/{id}/programs")
    public List<ProgramDto> getCommunityPrograms(@PathVariable Long id) {
        return programService.getCommunityPrograms(id);
    }

    @PostMapping("/{id}/programs")
    public ResponseEntity<ProgramDto> createCommunityProgram(@PathVariable Long id,
                                                              @RequestBody CreateProgramRequest req,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(programService.createCommunityProgram(id, req, user));
    }

    @DeleteMapping("/{id}/programs/{pid}")
    public ResponseEntity<Void> deleteCommunityProgram(@PathVariable Long id,
                                                        @PathVariable Long pid,
                                                        @AuthenticationPrincipal User user) {
        programService.deleteCommunityProgram(id, pid, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/programs/{pid}/sessions")
    public List<ProgramSessionDto> getProgramSessions(@PathVariable Long id, @PathVariable Long pid) {
        return programService.getProgramSessions(pid);
    }

    @PostMapping("/{id}/programs/{pid}/sessions")
    public ProgramSessionDto addProgramSession(@PathVariable Long id,
                                                @PathVariable Long pid,
                                                @RequestBody ProgramSessionDto dto,
                                                @AuthenticationPrincipal User user) {
        return programService.addSession(pid, dto, user);
    }

    @PostMapping("/{id}/programs/{pid}/enroll")
    public ProgramProgressDto enrollInProgram(@PathVariable Long id,
                                               @PathVariable Long pid,
                                               @AuthenticationPrincipal User user) {
        return programService.startProgram(pid, user.getEmail());
    }

    @GetMapping("/{id}/programs/{pid}/enrollees")
    public List<EnrolleeProgressDto> getProgramEnrollees(@PathVariable Long id, @PathVariable Long pid) {
        return programService.getProgramEnrollees(pid);
    }

    @PostMapping("/{id}/programs/{pid}/complete-session")
    public ProgramProgressDto completeSession(@PathVariable Long id,
                                               @PathVariable Long pid,
                                               @AuthenticationPrincipal User user) {
        return programService.completeSession(pid, user.getEmail());
    }

    // ── Join Requests ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/request-join")
    public ResponseEntity<Void> requestJoin(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        communityService.requestJoin(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/join-requests")
    public ResponseEntity<List<JoinRequestDto>> getJoinRequests(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(communityService.getJoinRequests(id));
    }

    @PostMapping("/{id}/requests/{requestId}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable Long id, @PathVariable Long requestId,
            @AuthenticationPrincipal User user) {
        communityService.approveJoinRequest(id, requestId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/requests/{requestId}/decline")
    public ResponseEntity<Void> declineRequest(@PathVariable Long id, @PathVariable Long requestId,
            @AuthenticationPrincipal User user) {
        communityService.declineJoinRequest(id, requestId, user);
        return ResponseEntity.ok().build();
    }

    // ── Weekly Digest ──────────────────────────────────────────────────────────

    @PostMapping("/{id}/digest")
    public ResponseEntity<PostDto> generateWeeklyDigest(@PathVariable Long id,
                                                         @AuthenticationPrincipal User user) {
        PostDto post = weeklyDigestService.generateDigest(id, user);
        return ResponseEntity.ok(post);
    }

    // ── Sponsors ─────────────────────────────────────────────────────────────

    @GetMapping("/{id}/sponsors")
    public List<SponsorDto> getSponsors(@PathVariable Long id) {
        return communityService.getSponsors(id);
    }

    @PostMapping("/{id}/sponsors")
    public ResponseEntity<SponsorDto> addSponsor(@PathVariable Long id,
                                                   @RequestBody SponsorDto sponsor,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communityService.addSponsor(id, sponsor, user));
    }

    @DeleteMapping("/{id}/sponsors/{sponsorId}")
    public ResponseEntity<Void> removeSponsor(@PathVariable Long id,
                                                @PathVariable Long sponsorId,
                                                @AuthenticationPrincipal User user) {
        communityService.removeSponsor(id, sponsorId, user);
        return ResponseEntity.ok().build();
    }
}
