/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.*;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

@Log4j2
@RequiredArgsConstructor
@Service
public class EventService {

    private static final String LOG_PREFIX = "[EventService]";

    @NonNull
    private EventRepository eventRepository;
    @NonNull
    private ParticipantRepository participantRepository;
    @NonNull
    private CustomEventPublisher customEventPublisher;
    @NonNull
    private MessageSource messageSource;

    public Event getEvent(String id, Locale usersLocale, Optional<Supplier<? extends RuntimeException>> errorSupplier) {
        Supplier<EventNotFoundException> defaultErrorSupplier = () ->
            new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), id));
        return eventRepository.findById(id).orElseThrow(errorSupplier.isPresent() ? errorSupplier.get() : defaultErrorSupplier)
            .toEvent();
    }

    public List<Event> getAllEventsForUser(String user) {
        return eventRepository.findAllByOwner(user);
    }

    public List<Participant> getParticipants(String eventId) {
        return participantRepository.findAllByEvent(eventId);
    }

    public Event createNewEvent(Event event, User loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        event.setOwner(loggedInUser.getUsername());
        if (StringUtils.hasText(event.getBannerImg())) {
            event.setBannerImg(String.format("%s_%s", loggedInUser.getUsername(), event.getBannerImg()));
        }
        if (event.getForm().getUserCreated() == null) {
            event.getForm().setUserCreated(loggedInUser.getUsername());
        }
        if (event.getForm().getDateCreated() == null) {
            event.getForm().setDateCreated(Instant.now());
        }
        customEventPublisher.publishSavedEventEvent(event, loggedInUser, usersLocale, userTimeZone);
        return eventRepository.save(event.toDto()).toEvent();
    }

    public Event editExistingEvent(Event newEvent, User loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        Event oldEvent = eventRepository.findById(newEvent.getId()).orElseThrow(() -> {
            log.error(String.format("%s Unable to edit Existing event. Old event with id <%s> was not found!", LOG_PREFIX, newEvent.getId()));
            throw new EventNotFoundException(newEvent.getId());
        }).toEvent();
        newEvent.setId(oldEvent.getId());
        customEventPublisher.publishSavedEventEvent(newEvent, loggedInUser, usersLocale, userTimeZone);
        return eventRepository.save(newEvent.toDto()).toEvent();
    }

    public void removeEventAndParticipants(String eventId) {
        eventRepository.deleteById(eventId);
        participantRepository.deleteAllByEvent(eventId);
    }

    public boolean eventExists(String eventId) {
        return eventRepository.existsById(eventId);
    }
}
