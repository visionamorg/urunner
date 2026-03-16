package com.runhub.rooms.controller;

import com.runhub.rooms.dto.CreateRoomRequest;
import com.runhub.rooms.dto.RoomDto;
import com.runhub.rooms.dto.RoomMemberDto;
import com.runhub.rooms.service.RoomService;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities/{communityId}/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<RoomDto> getRooms(@PathVariable Long communityId,
                                  @AuthenticationPrincipal User user) {
        return roomService.getRoomsForUser(communityId, user);
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long communityId,
                                              @RequestBody CreateRoomRequest request,
                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(communityId, request, user));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long communityId,
                                           @PathVariable Long roomId,
                                           @AuthenticationPrincipal User user) {
        roomService.deleteRoom(communityId, roomId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}/members")
    public List<RoomMemberDto> getMembers(@PathVariable Long communityId,
                                          @PathVariable Long roomId,
                                          @AuthenticationPrincipal User user) {
        return roomService.getMembers(communityId, roomId, user);
    }

    @PostMapping("/{roomId}/members")
    public ResponseEntity<Void> addMember(@PathVariable Long communityId,
                                          @PathVariable Long roomId,
                                          @RequestBody Map<String, Long> body,
                                          @AuthenticationPrincipal User user) {
        roomService.addMember(communityId, roomId, body.get("userId"), user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roomId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long communityId,
                                             @PathVariable Long roomId,
                                             @PathVariable Long userId,
                                             @AuthenticationPrincipal User user) {
        roomService.removeMember(communityId, roomId, userId, user);
        return ResponseEntity.ok().build();
    }
}
