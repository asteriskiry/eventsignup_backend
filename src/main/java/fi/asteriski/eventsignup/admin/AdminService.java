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

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
class AdminService {

    EventRepository eventRepository;
    ParticipantRepository participantRepository;
    EventService eventService;

    /**
     * Fetches all event from database in no particular order.
     * @return List of events.
     */
    List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Fetches all events belonging to a particular user.
     * @param userId Id of the user.
     * @return List of events ordered by starting date.
     */
    List<Event> getAllEventsForUser(String userId) {
        List<Event> events = eventService.getAllEventsForUser(userId);
        events.sort(Comparator.comparing(Event::getStartDate));
        return events;
    }

    /**
     * Fetched all participants from database.
     * @return List of participants in no particular order.
     */
    List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    /**
     * Fetched all participants for the specified event.
     * @param eventId Event's id.
     * @return List of participants in no particular order.
     */
    List<Participant> getAllParticipantsForEvent(String eventId) {
        return eventService.getParticipants(eventId);
    }
}
