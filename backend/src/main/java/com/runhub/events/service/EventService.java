package com.runhub.events.service;

import com.runhub.communities.model.Community;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.events.dto.CreateEventRequest;
import com.runhub.events.dto.EventDto;
import com.runhub.events.dto.EventParticipantDto;
import com.runhub.events.mapper.EventMapper;
import com.runhub.events.model.Event;
import com.runhub.events.model.EventRegistration;
import com.runhub.events.repository.EventRegistrationRepository;
import com.runhub.events.repository.EventRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CommunityRepository communityRepository;

    public List<EventDto> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc().stream()
                .map(e -> {
                    EventDto dto = eventMapper.toDto(e);
                    dto.setParticipantCount(registrationRepository.countActiveByEventId(e.getId()));
                    return dto;
                }).toList();
    }

    public EventDto getEventById(Long id) {
        Event event = findById(id);
        EventDto dto = eventMapper.toDto(event);
        dto.setParticipantCount(registrationRepository.countActiveByEventId(id));
        return dto;
    }

    @Transactional
    public EventDto createEvent(String email, CreateEventRequest request) {
        User organizer = userService.getUserEntityByEmail(email);
        Community community = null;
        if (request.getCommunityId() != null) {
            community = communityRepository.findById(request.getCommunityId()).orElse(null);
        }

        Event event = Event.builder()
                .name(request.getName())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .location(request.getLocation())
                .distanceKm(request.getDistanceKm())
                .price(request.getPrice() != null ? request.getPrice() : java.math.BigDecimal.ZERO)
                .maxParticipants(request.getMaxParticipants())
                .organizer(organizer)
                .community(community)
                .build();

        event = eventRepository.save(event);
        EventDto dto = eventMapper.toDto(event);
        dto.setParticipantCount(0L);
        return dto;
    }

    @Transactional
    public void registerForEvent(Long eventId, String email) {
        User user = userService.getUserEntityByEmail(email);
        Event event = findById(eventId);

        if (registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new BadRequestException("Already registered for this event");
        }

        if (event.getMaxParticipants() != null) {
            long count = registrationRepository.countActiveByEventId(eventId);
            if (count >= event.getMaxParticipants()) {
                throw new BadRequestException("Event is full");
            }
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(user)
                .status("REGISTERED")
                .build();
        registrationRepository.save(registration);
    }

    public List<EventParticipantDto> getEventParticipants(Long eventId) {
        findById(eventId);
        return registrationRepository.findByEventId(eventId)
                .stream().map(eventMapper::toParticipantDto).toList();
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }
}
