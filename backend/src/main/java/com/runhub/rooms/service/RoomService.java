package com.runhub.rooms.service;

import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.rooms.dto.CreateRoomRequest;
import com.runhub.rooms.dto.RoomDto;
import com.runhub.rooms.dto.RoomMemberDto;
import com.runhub.rooms.model.Room;
import com.runhub.rooms.model.RoomMember;
import com.runhub.rooms.model.RoomMemberId;
import com.runhub.rooms.repository.RoomMemberRepository;
import com.runhub.rooms.repository.RoomRepository;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<RoomDto> getRoomsForUser(Long communityId, User user) {
        String role = getCommunityRole(communityId, user.getId());
        boolean isAdmin = "ADMIN".equals(role);

        List<Room> rooms;
        if (isAdmin) {
            rooms = roomRepository.findByCommunityId(communityId);
        } else {
            rooms = roomRepository.findByCommunityId(communityId).stream()
                    .filter(r -> !r.getIsPrivate() ||
                            roomMemberRepository.existsByIdRoomIdAndIdUserId(r.getId(), user.getId()))
                    .toList();
        }

        return rooms.stream().map(r -> toDto(r)).toList();
    }

    @Transactional
    public RoomDto createRoom(Long communityId, CreateRoomRequest request, User admin) {
        requireCommunityAdmin(communityId, admin);

        var community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found: " + communityId));

        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .community(community)
                .createdBy(admin)
                .isPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : true)
                .build();
        room = roomRepository.save(room);

        // Auto-add creator as ADMIN room member
        RoomMember member = RoomMember.builder()
                .id(new RoomMemberId(room.getId(), admin.getId()))
                .room(room).user(admin).role("ADMIN").build();
        roomMemberRepository.save(member);

        return toDto(room);
    }

    @Transactional
    public void deleteRoom(Long communityId, Long roomId, User admin) {
        requireCommunityAdmin(communityId, admin);
        Room room = findRoom(roomId, communityId);
        roomRepository.delete(room);
    }

    public List<RoomMemberDto> getMembers(Long communityId, Long roomId, User user) {
        Room room = findRoom(roomId, communityId);
        String role = getCommunityRole(communityId, user.getId());
        boolean isAdmin = "ADMIN".equals(role);
        boolean isMember = roomMemberRepository.existsByIdRoomIdAndIdUserId(roomId, user.getId());

        if (!isAdmin && !isMember) {
            throw new BadRequestException("You are not a member of this room");
        }

        return roomMemberRepository.findByRoomId(roomId).stream()
                .map(this::toMemberDto).toList();
    }

    @Transactional
    public void addMember(Long communityId, Long roomId, Long targetUserId, User admin) {
        requireCommunityAdmin(communityId, admin);
        Room room = findRoom(roomId, communityId);

        if (!communityMemberRepository.existsByIdCommunityIdAndIdUserId(communityId, targetUserId))
            throw new BadRequestException("Target user is not a member of this community");
        if (roomMemberRepository.existsByIdRoomIdAndIdUserId(roomId, targetUserId))
            throw new BadRequestException("User is already a member of this room");

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

        RoomMember member = RoomMember.builder()
                .id(new RoomMemberId(roomId, targetUserId))
                .room(room).user(target).role("MEMBER").build();
        roomMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long communityId, Long roomId, Long targetUserId, User admin) {
        requireCommunityAdmin(communityId, admin);
        findRoom(roomId, communityId);
        roomMemberRepository.deleteByIdRoomIdAndIdUserId(roomId, targetUserId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Room findRoom(Long roomId, Long communityId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));
        if (!communityId.equals(room.getCommunity().getId()))
            throw new BadRequestException("Room does not belong to this community");
        return room;
    }

    private void requireCommunityAdmin(Long communityId, User user) {
        String role = getCommunityRole(communityId, user.getId());
        if (!"ADMIN".equals(role))
            throw new BadRequestException("Only community admins can manage rooms");
    }

    private String getCommunityRole(Long communityId, Long userId) {
        return communityMemberRepository.findByCommunityId(communityId).stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .map(CommunityMember::getRole)
                .findFirst().orElse(null);
    }

    private RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setDescription(room.getDescription());
        dto.setCommunityId(room.getCommunity().getId());
        dto.setCreatedByUsername(room.getCreatedBy().getDisplayUsername());
        dto.setIsPrivate(room.getIsPrivate());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setMemberCount(roomMemberRepository.countByRoomId(room.getId()));
        return dto;
    }

    private RoomMemberDto toMemberDto(RoomMember member) {
        RoomMemberDto dto = new RoomMemberDto();
        dto.setUserId(member.getUser().getId());
        dto.setUsername(member.getUser().getDisplayUsername());
        dto.setFirstName(member.getUser().getFirstName());
        dto.setLastName(member.getUser().getLastName());
        dto.setProfileImageUrl(member.getUser().getProfileImageUrl());
        dto.setRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());

        String fn = member.getUser().getFirstName();
        String ln = member.getUser().getLastName();
        String initials = "";
        if (fn != null && !fn.isEmpty()) initials += fn.charAt(0);
        if (ln != null && !ln.isEmpty()) initials += ln.charAt(0);
        dto.setInitials(initials.toUpperCase());
        return dto;
    }
}
