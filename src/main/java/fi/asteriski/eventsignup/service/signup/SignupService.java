/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.domain.signup.ParticipantDto;
import fi.asteriski.eventsignup.domain.signup.SignupEvent;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

public interface SignupService {
    SignupEvent getEventForSignUp(String eventId, Locale usersLocale, ZoneId userTimeZone);

    ParticipantDto addParticipantToEvent(String eventId, ParticipantDto participantDto, Locale usersLocale, ZoneId userTimeZone);

    void removeParticipantFromEvent(String eventId, String participantId, Locale usersLocale, ZoneId userTimeZone);

    List<SignupEvent> getUpcomingEvents(String days);
}
