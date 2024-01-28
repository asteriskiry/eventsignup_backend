/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.admin;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.event.EventEntity;
import fi.asteriski.eventsignup.domain.signup.ParticipantDto;
import fi.asteriski.eventsignup.domain.signup.ParticipantEntity;
import fi.asteriski.eventsignup.repo.event.EventRepository;
import fi.asteriski.eventsignup.repo.signup.ParticipantRepository;
import fi.asteriski.eventsignup.service.event.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
class AdminService {

    private static final String LOG_PREFIX = "[AdminService]";

    private EventRepository eventRepository;
    private ParticipantRepository participantRepository;
    private EventService eventService;

    /**
     * Fetches all event from database in no particular order.
     * @return List of events.
     */
    List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
            .map(EventEntity::toDto)
            .toList();
    }

    /**
     * Fetches all events belonging to a particular user.
     * @param userId ID of the user.
     * @return List of events ordered by starting date.
     */
    List<EventDto> getAllEventsForUser(String userId) {
        log.info(String.format("%s In %s.getAllEventsForUser(). User: %s", LOG_PREFIX, this.getClass().getSimpleName(), userId));
        return eventService.getAllEventsForUser(userId).stream()
            .sorted(Comparator.comparing(EventDto::getStartDate))
            .toList();
    }

    /**
     * Fetches all participants from database.
     * @return List of participants in no particular order.
     */
    List<ParticipantDto> getAllParticipants() {
        return participantRepository.findAll().stream().map(ParticipantEntity::toDto).toList();
    }

    /**
     * Fetched all participants for the specified event.
     * @param eventId Event's id.
     * @return List of participants in no particular order.
     */
    List<ParticipantDto> getAllParticipantsForEvent(String eventId) {
        return eventService.getParticipants(eventId);
    }
}
