/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.ArchivedEvent;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@Log4j2
@AllArgsConstructor
@Service
public class EventService {

    private static final String LOG_PREFIX = "[EventService]";

    private EventRepository eventRepository;
    private ArchivedEventRepository archivedEventRepository;
    private ParticipantRepository participantRepository;
    private CustomEventPublisher customEventPublisher;
    private MessageSource messageSource;

    public Event getEvent(String id, Locale usersLocale) {
        return eventRepository.findById(id).orElseThrow(() ->
            new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), id)));
    }

    public List<Event> getAllEventsForUser(String user) {
        return eventRepository.findAllByOwner(user);
    }

    public List<Participant> getParticipants(String eventId) {
        return participantRepository.findAllByEvent(eventId);
    }

    public Event createNewEvent(Event event, User loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        event.setOwner(loggedInUser.getUsername());
        if (event.getBannerImg() != null) {
            event.setBannerImg(String.format("%s_%s", loggedInUser.getUsername(), event.getBannerImg()));
        }
        if (event.getForm().getUserCreated() == null) {
            event.getForm().setUserCreated(loggedInUser.getUsername());
        }
        if (event.getForm().getDateCreated() == null) {
            event.getForm().setDateCreated(Instant.now());
        }
        customEventPublisher.publishSavedEventEvent(event, loggedInUser, usersLocale, userTimeZone);
        return eventRepository.save(event);
    }

    public Event editExistingEvent(Event newEvent, User loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        Event oldEvent = eventRepository.findById(newEvent.getId()).orElseThrow(() -> {
            log.error(String.format("%s Unable to edit Existing event. Old event with id <%s> was not found!", LOG_PREFIX, newEvent.getId()));
            throw new EventNotFoundException(newEvent.getId());
        });
        newEvent.setId(oldEvent.getId());
        customEventPublisher.publishSavedEventEvent(newEvent, loggedInUser, usersLocale, userTimeZone);
        return eventRepository.save(newEvent);
    }

    public ArchivedEvent archiveEvent(String eventId) {
        Event oldEvent = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error(String.format("%s Unable to archive event. Old event with id <%s> was not found!", LOG_PREFIX, eventId));
            throw new EventNotFoundException(eventId);
        });
        long numberOfParticipants = participantRepository.countAllByEvent(eventId);
        ArchivedEvent archivedEvent = new ArchivedEvent(oldEvent, Instant.now(), numberOfParticipants);
        archivedEvent = archivedEventRepository.save(archivedEvent);
        removeEventAndParticipants(eventId);
        return archivedEvent;
    }

    public void removeEventAndParticipants(String eventId) {
        eventRepository.deleteById(eventId);
        participantRepository.deleteAllByEvent(eventId);
    }

    public boolean eventExists(String eventId) {
        return eventRepository.existsById(eventId);
    }
}
