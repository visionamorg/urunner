package com.runhub.communities.service;

import com.runhub.communities.dto.CommunityDto;
import com.runhub.communities.dto.CommunityMemberDto;
import com.runhub.communities.dto.CreateCommunityRequest;
import com.runhub.communities.dto.UpdateCommunityRequest;
import com.runhub.communities.mapper.CommunityMapper;
import com.runhub.communities.model.Community;
import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.model.CommunityMemberId;
import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.service.FeedService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityMapper communityMapper;
    private final UserService userService;
    private final GoogleDriveService googleDriveService;
    private final FeedService feedService;

    public List<CommunityDto> getAllCommunities(User user) {
        return communityRepository.findAllByOrderByMemberCountDesc()
                .stream()
                .map(c -> toDtoWithMembership(c, user))
                .toList();
    }

    public List<CommunityDto> getAllCommunities() {
        return communityRepository.findAllByOrderByMemberCountDesc()
                .stream()
                .map(communityMapper::toDto)
                .toList();
    }

    public CommunityDto getCommunity(Long id, User user) {
        Community community = findById(id);
        return toDtoWithMembership(community, user);
    }

    public CommunityDto getCommunityById(Long id) {
        return communityMapper.toDto(findById(id));
    }

    @Transactional
    public CommunityDto createCommunity(CreateCommunityRequest request, User user) {
        Community community = Community.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .coverUrl(request.getCoverUrl())
                .driveFolderId(request.getDriveFolderId())
                .creator(user)
                .memberCount(1)
                .build();
        community = communityRepository.save(community);

        CommunityMember member = CommunityMember.builder()
                .id(new CommunityMemberId(community.getId(), user.getId()))
                .community(community)
                .user(user)
                .role("ADMIN")
                .build();
        communityMemberRepository.save(member);

        CommunityDto dto = communityMapper.toDto(community);
        dto.setJoined(true);
        dto.setRole("ADMIN");
        dto.setAdmin(true);
        return dto;
    }

    @Transactional
    public CommunityDto createCommunity(String email, CreateCommunityRequest request) {
        User user = userService.getUserEntityByEmail(email);
        return createCommunity(request, user);
    }

    @Transactional
    public CommunityDto updateCommunity(Long id, UpdateCommunityRequest request, User user) {
        Community community = findById(id);
        String userRole = getMemberRole(id, user.getId());

        if (!"ADMIN".equals(userRole) && !community.getCreator().getId().equals(user.getId())) {
            throw new BadRequestException("Only community admins can update community settings");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            community.setName(request.getName());
        }
        if (request.getDescription() != null) {
            community.setDescription(request.getDescription());
        }
        if (request.getDriveFolderId() != null) {
            community.setDriveFolderId(request.getDriveFolderId());
        }
        if (request.getCoverUrl() != null) {
            community.setCoverUrl(request.getCoverUrl());
        }
        if (request.getImageUrl() != null) {
            community.setImageUrl(request.getImageUrl());
        }

        Community saved = communityRepository.save(community);
        return toDtoWithMembership(saved, user);
    }

    @Transactional
    public void joinCommunity(Long communityId, User user) {
        Community community = findById(communityId);

        if (communityMemberRepository.existsByIdCommunityIdAndIdUserId(communityId, user.getId())) {
            throw new BadRequestException("Already a member of this community");
        }

        CommunityMember member = CommunityMember.builder()
                .id(new CommunityMemberId(communityId, user.getId()))
                .community(community)
                .user(user)
                .role("MEMBER")
                .build();
        communityMemberRepository.save(member);
        community.setMemberCount(community.getMemberCount() + 1);
        communityRepository.save(community);
    }

    @Transactional
    public void joinCommunity(Long communityId, String email) {
        User user = userService.getUserEntityByEmail(email);
        joinCommunity(communityId, user);
    }

    @Transactional
    public void leaveCommunity(Long communityId, User user) {
        Community community = findById(communityId);

        if (!communityMemberRepository.existsByIdCommunityIdAndIdUserId(communityId, user.getId())) {
            throw new BadRequestException("You are not a member of this community");
        }

        if (community.getCreator().getId().equals(user.getId())) {
            throw new BadRequestException("Community creator cannot leave the community");
        }

        CommunityMemberId memberId = new CommunityMemberId(communityId, user.getId());
        communityMemberRepository.deleteById(memberId);
        community.setMemberCount(Math.max(0, community.getMemberCount() - 1));
        communityRepository.save(community);
    }

    public List<CommunityMemberDto> getMembers(Long communityId) {
        findById(communityId);
        return communityMemberRepository.findByCommunityId(communityId)
                .stream().map(communityMapper::toMemberDto).toList();
    }

    public List<CommunityMemberDto> getCommunityMembers(Long communityId) {
        return getMembers(communityId);
    }

    @Transactional
    public PostDto syncDrivePhotos(Long communityId, User user) {
        Community community = findById(communityId);

        String userRole = getMemberRole(communityId, user.getId());
        if (!"ADMIN".equals(userRole) && !community.getCreator().getId().equals(user.getId())) {
            throw new BadRequestException("Only community admins can sync Drive photos");
        }

        if (community.getDriveFolderId() == null || community.getDriveFolderId().isBlank()) {
            throw new BadRequestException(
                "No Google Drive folder configured for this community. Please set a Drive Folder ID in Settings.");
        }

        List<String> imageUrls = googleDriveService.getImagesFromFolder(community.getDriveFolderId());

        if (imageUrls.isEmpty()) {
            throw new BadRequestException("No images found in the Google Drive folder");
        }

        return feedService.createPhotoPost(communityId, imageUrls, "Photos from Google Drive", user);
    }

    private CommunityDto toDtoWithMembership(Community community, User user) {
        CommunityDto dto = communityMapper.toDto(community);

        if (user != null) {
            boolean joined = communityMemberRepository.existsByIdCommunityIdAndIdUserId(community.getId(), user.getId());
            dto.setJoined(joined);

            if (joined) {
                String role = getMemberRole(community.getId(), user.getId());
                dto.setRole(role);
                dto.setAdmin("ADMIN".equals(role) || community.getCreator().getId().equals(user.getId()));
            } else {
                dto.setAdmin(community.getCreator().getId().equals(user.getId()));
            }
        }

        return dto;
    }

    private String getMemberRole(Long communityId, Long userId) {
        return communityMemberRepository.findByCommunityId(communityId)
                .stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .map(CommunityMember::getRole)
                .findFirst()
                .orElse(null);
    }

    private Community findById(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + id));
    }
}
