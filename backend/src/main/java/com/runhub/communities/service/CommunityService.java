package com.runhub.communities.service;

import com.runhub.communities.dto.CommunityDto;
import com.runhub.communities.dto.CommunityMemberDto;
import com.runhub.communities.dto.CreateCommunityRequest;
import com.runhub.communities.mapper.CommunityMapper;
import com.runhub.communities.model.Community;
import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.model.CommunityMemberId;
import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
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

    public List<CommunityDto> getAllCommunities() {
        return communityRepository.findAllByOrderByMemberCountDesc()
                .stream().map(communityMapper::toDto).toList();
    }

    public CommunityDto getCommunityById(Long id) {
        return communityMapper.toDto(findById(id));
    }

    @Transactional
    public CommunityDto createCommunity(String email, CreateCommunityRequest request) {
        User user = userService.getUserEntityByEmail(email);
        Community community = Community.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .creator(user)
                .memberCount(1)
                .build();
        community = communityRepository.save(community);

        // Creator auto-joins as ADMIN
        CommunityMember member = CommunityMember.builder()
                .id(new CommunityMemberId(community.getId(), user.getId()))
                .community(community)
                .user(user)
                .role("ADMIN")
                .build();
        communityMemberRepository.save(member);

        return communityMapper.toDto(community);
    }

    @Transactional
    public void joinCommunity(Long communityId, String email) {
        User user = userService.getUserEntityByEmail(email);
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

    public List<CommunityMemberDto> getCommunityMembers(Long communityId) {
        findById(communityId);
        return communityMemberRepository.findByCommunityId(communityId)
                .stream().map(communityMapper::toMemberDto).toList();
    }

    private Community findById(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + id));
    }
}
