package com.runhub.communities.mapper;

import com.runhub.communities.dto.CommunityDto;
import com.runhub.communities.dto.CommunityMemberDto;
import com.runhub.communities.model.Community;
import com.runhub.communities.model.CommunityMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(source = "creator.id", target = "creatorId")
    @Mapping(source = "creator.displayUsername", target = "creatorUsername")
    CommunityDto toDto(Community community);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.displayUsername", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.profileImageUrl", target = "profileImageUrl")
    CommunityMemberDto toMemberDto(CommunityMember member);
}
