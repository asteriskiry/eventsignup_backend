/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.ArchivedEvent;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ArchivedEventRepository archivedEventRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    public Event getEvent(String id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    public List<Event> getAllEventsForUser(String user) {
        return eventRepository.findAllByOwner(user);
    }

    public List<Participant> getParticipants(String eventId) {
        return participantRepository.findAllByEvent(eventId);
    }

    public Event createNewEvent(Event event, String userName) {
        event.setOwner(userName);
        if (event.getBannerImg() != null) {
            event.setBannerImg(String.format("%s_%s", userName, event.getBannerImg()));
        }
        if (event.getForm().getUserCreated() == null) {
            event.getForm().setUserCreated(userName);
        }
        if (event.getForm().getDateCreated() == null) {
            event.getForm().setDateCreated(Instant.now());
        }
        return eventRepository.save(event);
    }

    public Event editExistingEvent(Event newEvent) {
        Event oldEvent = eventRepository.findById(newEvent.getId()).orElseThrow(() -> new EventNotFoundException(newEvent.getId()));
        newEvent.setId(oldEvent.getId());
        return eventRepository.save(newEvent);
    }

    public ArchivedEvent archiveEvent(String eventId) {
        Event oldEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
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
