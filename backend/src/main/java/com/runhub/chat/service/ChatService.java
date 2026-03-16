package com.runhub.chat.service;

import com.runhub.chat.dto.MessageDto;
import com.runhub.chat.dto.SendMessageRequest;
import com.runhub.chat.mapper.MessageMapper;
import com.runhub.chat.model.Message;
import com.runhub.chat.repository.MessageRepository;
import com.runhub.communities.model.Community;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.events.model.Event;
import com.runhub.events.repository.EventRepository;
import com.runhub.rooms.model.Room;
import com.runhub.rooms.repository.RoomMemberRepository;
import com.runhub.rooms.repository.RoomRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final CommunityRepository communityRepository;
    private final EventRepository eventRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    public List<MessageDto> getMessages(Long communityId, Long eventId, Long roomId) {
        if (roomId != null) {
            return messageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                    .stream().map(messageMapper::toDto).toList();
        } else if (communityId != null) {
            return messageRepository.findByCommunityIdOrderBySentAtAsc(communityId)
                    .stream().map(messageMapper::toDto).toList();
        } else if (eventId != null) {
            return messageRepository.findByEventIdOrderBySentAtAsc(eventId)
                    .stream().map(messageMapper::toDto).toList();
        }
        throw new BadRequestException("communityId, eventId, or roomId required");
    }

    @Transactional
    public MessageDto sendMessage(String email, SendMessageRequest request) {
        User sender = userService.getUserEntityByEmail(email);

        if ((request.getContent() == null || request.getContent().isBlank()) &&
            (request.getMediaUrl() == null || request.getMediaUrl().isBlank())) {
            throw new BadRequestException("Message must have content or media");
        }

        Community community = null;
        Event event = null;
        Room room = null;

        if (request.getCommunityId() != null) {
            community = communityRepository.findById(request.getCommunityId()).orElse(null);
        }
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId()).orElse(null);
        }
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId()).orElse(null);
            if (room != null && room.getIsPrivate()) {
                boolean isMember = roomMemberRepository.existsByIdRoomIdAndIdUserId(room.getId(), sender.getId());
                if (!isMember) {
                    throw new BadRequestException("You are not a member of this room");
                }
            }
        }

        Message message = Message.builder()
                .sender(sender)
                .community(community)
                .event(event)
                .room(room)
                .content(request.getContent() != null ? request.getContent() : "")
                .mediaUrl(request.getMediaUrl())
                .mediaType(request.getMediaType())
                .build();

        return messageMapper.toDto(messageRepository.save(message));
    }
}
