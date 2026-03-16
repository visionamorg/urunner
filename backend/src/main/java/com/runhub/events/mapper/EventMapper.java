package com.runhub.events.mapper;

import com.runhub.events.dto.EventDto;
import com.runhub.events.dto.EventParticipantDto;
import com.runhub.events.model.Event;
import com.runhub.events.model.EventRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "organizer.id", target = "organizerId")
    @Mapping(source = "organizer.displayUsername", target = "organizerUsername")
    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "community.name", target = "communityName")
    @Mapping(target = "participantCount", ignore = true)
    @Mapping(target = "photoUrls", ignore = true)
    EventDto toDto(Event event);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.displayUsername", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.profileImageUrl", target = "profileImageUrl")
    EventParticipantDto toParticipantDto(EventRegistration registration);
}
