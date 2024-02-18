/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.admin;

import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.service.event.EventService;
import fi.asteriski.eventsignup.service.signup.ParticipantService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final String LOG_PREFIX = "[AdminServiceImpl]";

    private ParticipantService participantService;
    private EventService eventService;

    /**
     * Fetches all event from database in no particular order.
     * @return List of events.
     */
    @Override
    public List<EventDto> getAllEvents() {
        return eventService.findAll();
    }

    /**
     * Fetches all events belonging to a particular user.
     * @param userId ID of the user.
     * @return List of events ordered by starting date.
     */
    @Override
    public List<EventDto> getAllEventsForUser(String userId) {
        log.info(String.format("%s In %s.getAllEventsForUser(). User: %s", LOG_PREFIX, this.getClass().getSimpleName(), userId));
        return eventService.getAllEventsForUser(userId).stream()
            .sorted(Comparator.comparing(EventDto::getStartDate))
            .toList();
    }

    /**
     * Fetches all participants from database.
     * @return List of participants in no particular order.
     */
    @Override
    public List<ParticipantDto> getAllParticipants() {
        return participantService.findAll();
    }

    /**
     * Fetched all participants for the specified event.
     * @param eventId Event's id.
     * @return List of participants in no particular order.
     */
    @Override
    public List<ParticipantDto> getAllParticipantsForEvent(String eventId) {
        return eventService.getParticipants(eventId);
    }
}
