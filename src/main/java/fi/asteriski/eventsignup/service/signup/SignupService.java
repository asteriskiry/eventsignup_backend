/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.model.signup.SignupEvent;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public interface SignupService {
    SignupEvent getEventForSignUp(UUID eventId, Locale usersLocale, ZoneId userTimeZone);

    ParticipantDto addParticipantToEvent(
            UUID eventId, ParticipantDto participantDto, Locale usersLocale, ZoneId userTimeZone);

    void removeParticipantFromEvent(UUID eventId, UUID participantId, Locale usersLocale, ZoneId userTimeZone);

    List<SignupEvent> getUpcomingEvents(String days);
}
