/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.admin;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.event.EventRepository;
import fi.asteriski.eventsignup.event.EventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    EventRepository eventRepository;
    ParticipantRepository participantRepository;
    EventService eventService;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEventsForUser(String userId) {
        return eventService.getAllEventsForUser(userId);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public List<Participant> getAllParticipantsForEvent(String eventId) {
        return eventService.getParticipants(eventId);
    }
}
