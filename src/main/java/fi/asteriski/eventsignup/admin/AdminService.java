/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.admin;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.EventDto;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.event.EventRepository;
import fi.asteriski.eventsignup.event.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
class AdminService {

    private static final String LOG_PREFIX = "[AdminService]";

    EventRepository eventRepository;
    ParticipantRepository participantRepository;
    EventService eventService;

    /**
     * Fetches all event from database in no particular order.
     * @return List of events.
     */
    List<Event> getAllEvents() {
        return eventRepository.findAll().stream()
            .map(EventDto::toEvent)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Fetches all events belonging to a particular user.
     * @param userId Id of the user.
     * @return List of events ordered by starting date.
     */
    List<Event> getAllEventsForUser(String userId) {
        log.info(String.format("%s In %s.getAllEventsForUser(). User: %s", LOG_PREFIX, this.getClass().getSimpleName(), userId));
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
