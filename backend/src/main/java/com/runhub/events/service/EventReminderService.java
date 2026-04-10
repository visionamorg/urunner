package com.runhub.events.service;

import com.runhub.events.model.Event;
import com.runhub.events.repository.EventRepository;
import com.runhub.events.repository.EventRegistrationRepository;
import com.runhub.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventReminderService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d 'at' h:mm a");

    @Scheduled(cron = "0 0 8 * * *")
    public void sendEventReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusHours(24);
        LocalDateTime to   = now.plusHours(26);

        List<Event> upcoming = eventRepository.findByEventDateBetween(from, to);
        for (Event event : upcoming) {
            registrationRepository.findByEventId(event.getId()).forEach(reg -> {
                if ("CONFIRMED".equals(reg.getStatus()) || "REGISTERED".equals(reg.getStatus())) {
                    String when = event.getEventDate() != null ? event.getEventDate().format(FMT) : "soon";
                    notificationService.create(
                        reg.getUser(),
                        "EVENT",
                        "Reminder: " + event.getName(),
                        "\"" + event.getName() + "\" starts tomorrow at " + when + ". Get ready!",
                        "/events/" + event.getId()
                    );
                }
            });
            log.info("Sent reminder for event '{}'", event.getName());
        }
    }
}
