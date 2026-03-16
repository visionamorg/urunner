package com.runhub.chat.mapper;

import com.runhub.chat.dto.MessageDto;
import com.runhub.chat.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.displayUsername", target = "senderUsername")
    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "event.id", target = "eventId")
    MessageDto toDto(Message message);
}
