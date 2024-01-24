/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.EventDto;
import fi.asteriski.eventsignup.domain.signup.Participant;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service
public class EventService {

    private static final String LOG_PREFIX = "[EventService]";
    private static Supplier<EventNotFoundException> defaultErrorSupplier;

    private EventRepository eventRepository;
    private ParticipantRepository participantRepository;
    private CustomEventPublisher customEventPublisher;
    private MessageSource messageSource;

    public Event getEvent(String id, Locale usersLocale, Optional<Supplier<? extends RuntimeException>> errorSupplier) {
        if (defaultErrorSupplier == null) {
            defaultErrorSupplier = () ->
                new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), id));
        }
        return eventRepository.findById(id).orElseThrow(errorSupplier.orElse(defaultErrorSupplier)).toEvent();
    }

    public List<Event> getAllEventsForUser(String user) {
        return eventRepository.findAllByOwner(user).stream()
            .map(EventDto::toEvent)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<Participant> getParticipants(String eventId) {
        return participantRepository.findAllByEvent(eventId);
    }

    public Event createNewEvent(Event event, Locale usersLocale, ZoneId userTimeZone) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        event.setOwner(authentication.getName());
        if (StringUtils.hasText(event.getBannerImg())) {
            event.setBannerImg(String.format("%s_%s", authentication.getName(), event.getBannerImg()));
        }
        if (event.getForm().getUserCreated() == null) {
            event.getForm().setUserCreated(authentication.getName());
        }
        if (event.getForm().getDateCreated() == null) {
            event.getForm().setDateCreated(Instant.now());
        }
        customEventPublisher.publishSavedEventEvent(event, authentication, usersLocale, userTimeZone);
        return eventRepository.save(event.toDto()).toEvent();
    }

    public Event editExistingEvent(Event newEvent, Locale usersLocale, ZoneId userTimeZone) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Event oldEvent = eventRepository.findById(newEvent.getId()).orElseThrow(() -> {
            log.error(String.format("%s Unable to edit Existing event. Old event with id <%s> was not found!", LOG_PREFIX, newEvent.getId()));
            throw new EventNotFoundException(newEvent.getId());
        }).toEvent();
        newEvent.setId(oldEvent.getId());
        customEventPublisher.publishSavedEventEvent(newEvent, authentication, usersLocale, userTimeZone);
        return eventRepository.save(newEvent.toDto()).toEvent();
    }

    public void removeEventAndParticipants(String eventId) {
        eventRepository.deleteById(eventId);
        participantRepository.deleteAllByEvent(eventId);
    }

    public boolean eventExists(String eventId) {
        return eventRepository.existsById(eventId);
    }

    public List<Event> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1) {
        return eventRepository.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit1).stream()
            .map(EventDto::toEvent)
            .toList();
    }

    public void deleteAllById(List<String> eventIds) {
        eventRepository.deleteAllById(eventIds);
    }
}
