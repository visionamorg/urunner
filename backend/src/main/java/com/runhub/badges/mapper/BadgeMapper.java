package com.runhub.badges.mapper;

import com.runhub.badges.dto.BadgeDto;
import com.runhub.badges.dto.UserBadgeDto;
import com.runhub.badges.model.Badge;
import com.runhub.badges.model.UserBadge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BadgeMapper {

    BadgeDto toDto(Badge badge);

    @Mapping(source = "badge.id", target = "badgeId")
    @Mapping(source = "badge.name", target = "badgeName")
    @Mapping(source = "badge.description", target = "badgeDescription")
    @Mapping(source = "badge.iconUrl", target = "badgeIconUrl")
    UserBadgeDto toUserBadgeDto(UserBadge userBadge);
}
