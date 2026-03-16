package com.runhub.events.service;

import com.runhub.communities.model.Community;
import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.events.dto.CreateEventRequest;
import com.runhub.events.dto.EventDto;
import com.runhub.events.dto.EventParticipantDto;
import com.runhub.events.dto.UpdateEventRequest;
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
    private final CommunityMemberRepository communityMemberRepository;

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

    // ── Community-scoped Event Methods ────────────────────────────────────────

    public List<EventDto> getCommunityEvents(Long communityId) {
        return eventRepository.findByCommunityIdOrderByEventDateAsc(communityId)
                .stream().map(e -> {
                    EventDto dto = eventMapper.toDto(e);
                    dto.setParticipantCount(registrationRepository.countActiveByEventId(e.getId()));
                    return dto;
                }).toList();
    }

    @Transactional
    public EventDto createCommunityEvent(Long communityId, String email, CreateEventRequest request) {
        User organizer = userService.getUserEntityByEmail(email);
        requireCommunityAdmin(communityId, organizer);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found: " + communityId));

        request.setCommunityId(communityId);

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
    public EventDto updateCommunityEvent(Long communityId, Long eventId, UpdateEventRequest request, String email) {
        User admin = userService.getUserEntityByEmail(email);
        requireCommunityAdmin(communityId, admin);

        Event event = findById(eventId);
        if (event.getCommunity() == null || !communityId.equals(event.getCommunity().getId()))
            throw new BadRequestException("Event does not belong to this community");

        if (request.getName() != null && !request.getName().isBlank()) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getLocation() != null && !request.getLocation().isBlank()) event.setLocation(request.getLocation());
        if (request.getDistanceKm() != null) event.setDistanceKm(request.getDistanceKm());
        if (request.getPrice() != null) event.setPrice(request.getPrice());
        if (request.getMaxParticipants() != null) event.setMaxParticipants(request.getMaxParticipants());

        event = eventRepository.save(event);
        EventDto dto = eventMapper.toDto(event);
        dto.setParticipantCount(registrationRepository.countActiveByEventId(event.getId()));
        return dto;
    }

    @Transactional
    public void cancelCommunityEvent(Long communityId, Long eventId, String email) {
        User admin = userService.getUserEntityByEmail(email);
        requireCommunityAdmin(communityId, admin);

        Event event = findById(eventId);
        if (event.getCommunity() == null || !communityId.equals(event.getCommunity().getId()))
            throw new BadRequestException("Event does not belong to this community");

        event.setIsCancelled(true);
        eventRepository.save(event);
    }

    private void requireCommunityAdmin(Long communityId, User user) {
        String role = communityMemberRepository.findByCommunityId(communityId).stream()
                .filter(m -> m.getUser().getId().equals(user.getId()))
                .map(CommunityMember::getRole)
                .findFirst().orElse(null);
        if (!"ADMIN".equals(role))
            throw new BadRequestException("Only community admins can manage events");
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }
}
