/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.admin;

import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import java.util.List;
import java.util.UUID;

public interface AdminService {
    List<EventDto> getAllEvents();

    List<EventDto> getAllEventsForUser(String userId);

    List<ParticipantDto> getAllParticipants();

    List<ParticipantDto> getAllParticipantsForEvent(UUID eventId);
}
