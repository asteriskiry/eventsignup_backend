/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.signup.ParticipantDto;
import fi.asteriski.eventsignup.exception.EventSignupException;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public interface EventService {
    EventDto getEvent(String id, Locale usersLocale, Optional<Supplier<? extends EventSignupException>> errorSupplier);

    List<EventDto> getAllEventsForUser(String user);

    List<ParticipantDto> getParticipants(String eventId);

    EventDto createNewEvent(EventDto eventDto, Locale usersLocale, ZoneId userTimeZone);

    EventDto editExistingEvent(EventDto newEventDto, Locale usersLocale, ZoneId userTimeZone);

    void removeEventAndParticipants(String eventId);

    boolean eventExists(String eventId);

    List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1);

    void deleteAllByIds(List<String> eventIds);

    List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2);

    List<EventDto> findAll();
}
