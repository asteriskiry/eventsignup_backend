/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.exception.EventSignupException;
import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public interface EventService {
    EventDto getEvent(UUID id, Locale usersLocale, Optional<Supplier<? extends EventSignupException>> errorSupplier);

    List<EventDto> getAllEventsForUser(String user);

    List<ParticipantDto> getParticipants(UUID eventId);

    EventDto createNewEvent(EventDto eventDto, Locale usersLocale, ZoneId userTimeZone);

    EventDto editExistingEvent(EventDto newEventDto, Locale usersLocale, ZoneId userTimeZone);

    void removeEventAndParticipants(UUID eventId);

    boolean eventExists(UUID eventId);

    List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1);

    void deleteAllByIds(List<UUID> eventIds);

    List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2);

    List<EventDto> findAll();
}
