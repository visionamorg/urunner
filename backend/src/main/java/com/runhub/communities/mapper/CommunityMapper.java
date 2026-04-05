package com.runhub.communities.mapper;

import com.runhub.communities.dto.CommunityDto;
import com.runhub.communities.dto.CommunityMemberDto;
import com.runhub.communities.model.Community;
import com.runhub.communities.model.CommunityMember;
import org.springframework.stereotype.Component;

@Component
public class CommunityMapper {

    public CommunityDto toDto(Community community) {
        CommunityDto dto = new CommunityDto();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setDescription(community.getDescription());
        dto.setImageUrl(community.getImageUrl());
        dto.setCoverUrl(community.getCoverUrl());
        dto.setDriveFolderId(community.getDriveFolderId());
        dto.setIsPrivate(community.getIsPrivate());
        dto.setMemberCount(community.getMemberCount());
        dto.setCreatedAt(community.getCreatedAt());

        if (community.getCreator() != null) {
            dto.setCreatorId(community.getCreator().getId());
            dto.setCreatorUsername(community.getCreator().getDisplayUsername());
        }

        dto.setLeaderboardMetric(community.getLeaderboardMetric() != null ? community.getLeaderboardMetric() : "DISTANCE");

        return dto;
    }

    public CommunityMemberDto toMemberDto(CommunityMember member) {
        CommunityMemberDto dto = new CommunityMemberDto();
        dto.setRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());

        if (member.getUser() != null) {
            dto.setUserId(member.getUser().getId());
            dto.setUsername(member.getUser().getDisplayUsername());
            dto.setFirstName(member.getUser().getFirstName());
            dto.setLastName(member.getUser().getLastName());
            dto.setProfileImageUrl(member.getUser().getProfileImageUrl());

            String firstName = member.getUser().getFirstName();
            String lastName = member.getUser().getLastName();
            String initials = "";
            if (firstName != null && !firstName.isEmpty()) initials += firstName.charAt(0);
            if (lastName != null && !lastName.isEmpty()) initials += lastName.charAt(0);
            dto.setInitials(initials.toUpperCase());
        }

        dto.setLeaderboardOptOut(member.getLeaderboardOptOut() != null && member.getLeaderboardOptOut());

        return dto;
    }
}
