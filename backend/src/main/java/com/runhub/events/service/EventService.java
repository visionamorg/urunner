package com.runhub.events.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.runhub.events.repository.EventGalleryRepository;
import com.runhub.events.repository.EventRegistrationRepository;
import com.runhub.events.repository.EventRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import com.runhub.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventGalleryRepository galleryRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    private List<String> parsePhotoUrls(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try { return objectMapper.readValue(json, new TypeReference<List<String>>() {}); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    private String serializePhotoUrls(List<String> urls) {
        if (urls == null || urls.isEmpty()) return null;
        try { return objectMapper.writeValueAsString(urls); }
        catch (Exception e) { return null; }
    }

    public List<EventDto> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc().stream()
                .map(this::enrichDto).toList();
    }

    public EventDto getEventById(Long id) {
        return enrichDto(findById(id));
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

        event.setPhotoUrls(serializePhotoUrls(request.getPhotoUrls()));
        event = eventRepository.save(event);
        return enrichDto(event);
    }

    @Transactional
    public EventParticipantDto registerForEvent(Long eventId, String email) {
        User user = userService.getUserEntityByEmail(email);
        Event event = findById(eventId);

        if (registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new BadRequestException("Already registered for this event");
        }

        String status;
        if (event.getPrice() != null && event.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
            status = "PENDING_PAYMENT";
        } else if (event.getMaxParticipants() != null) {
            long count = registrationRepository.countActiveByEventId(eventId);
            status = count >= event.getMaxParticipants() ? "WAITLISTED" : "CONFIRMED";
        } else {
            status = "CONFIRMED";
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(user)
                .status(status)
                .role("RUNNER")
                .build();
        registration = registrationRepository.save(registration);

        if ("WAITLISTED".equals(status)) {
            notificationService.create(user, "EVENT",
                    "Waitlisted for " + event.getName(),
                    "The event is full. You've been added to the waitlist and will be notified if a spot opens up.",
                    "/events/" + eventId);
        }

        return eventMapper.toParticipantDto(registration);
    }

    @Transactional
    public EventParticipantDto registerVolunteer(Long eventId, String email) {
        User user = userService.getUserEntityByEmail(email);
        Event event = findById(eventId);

        if (registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new BadRequestException("Already registered for this event");
        }

        if (event.getMaxVolunteers() != null) {
            long count = registrationRepository.countVolunteersByEventId(eventId);
            if (count >= event.getMaxVolunteers()) {
                throw new BadRequestException("Volunteer spots are full");
            }
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(user)
                .status("CONFIRMED")
                .role("VOLUNTEER")
                .build();
        registration = registrationRepository.save(registration);
        return eventMapper.toParticipantDto(registration);
    }

    @Transactional
    public void cancelRegistration(Long eventId, String email) {
        User user = userService.getUserEntityByEmail(email);
        Event event = findById(eventId);

        EventRegistration reg = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new BadRequestException("Not registered for this event"));

        boolean wasConfirmedRunner = "RUNNER".equals(reg.getRole())
                && ("CONFIRMED".equals(reg.getStatus()) || "REGISTERED".equals(reg.getStatus()));

        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);

        // Auto-promote first waitlisted user
        if (wasConfirmedRunner) {
            promoteFromWaitlist(event);
        }
    }

    private void promoteFromWaitlist(Event event) {
        List<EventRegistration> waitlisted = registrationRepository.findWaitlistedByEventId(event.getId());
        if (!waitlisted.isEmpty()) {
            EventRegistration next = waitlisted.get(0);
            next.setStatus("CONFIRMED");
            registrationRepository.save(next);
            notificationService.create(next.getUser(), "EVENT",
                    "You're in! Spot opened for " + event.getName(),
                    "A spot has opened up and you've been confirmed for the event!",
                    "/events/" + event.getId());
        }
    }

    public List<EventParticipantDto> getEventParticipants(Long eventId) {
        return getEventParticipants(eventId, null, null);
    }

    public List<EventParticipantDto> getEventParticipants(Long eventId, String role, String status) {
        findById(eventId);
        List<EventRegistration> regs;
        if (role != null && status != null) {
            regs = registrationRepository.findByEventIdAndRoleAndStatus(eventId, role, status);
        } else if (role != null) {
            regs = registrationRepository.findByEventIdAndRole(eventId, role);
        } else if (status != null) {
            regs = registrationRepository.findByEventIdAndStatus(eventId, status);
        } else {
            regs = registrationRepository.findByEventId(eventId);
        }
        return regs.stream().map(eventMapper::toParticipantDto).toList();
    }

    // ── Community-scoped Event Methods ────────────────────────────────────────

    public List<EventDto> getCommunityEvents(Long communityId) {
        return eventRepository.findByCommunityIdOrderByEventDateAsc(communityId)
                .stream().map(this::enrichDto).toList();
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

        event.setPhotoUrls(serializePhotoUrls(request.getPhotoUrls()));
        event = eventRepository.save(event);
        return enrichDto(event);
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
        if (request.getPhotoUrls() != null) event.setPhotoUrls(serializePhotoUrls(request.getPhotoUrls()));

        event = eventRepository.save(event);
        return enrichDto(event);
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

    private EventDto enrichDto(Event event) {
        EventDto dto = eventMapper.toDto(event);
        dto.setParticipantCount(registrationRepository.countActiveByEventId(event.getId()));
        dto.setVolunteersCount(registrationRepository.countVolunteersByEventId(event.getId()));
        dto.setWaitlistCount(registrationRepository.countWaitlistedByEventId(event.getId()));
        dto.setPhotoUrls(parsePhotoUrls(event.getPhotoUrls()));
        dto.setGalleryCount(galleryRepository.countByEventId(event.getId()));
        return dto;
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }
}
