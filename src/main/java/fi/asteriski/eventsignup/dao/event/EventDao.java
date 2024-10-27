/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.dao.event;

import fi.asteriski.eventsignup.model.event.EventDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventDao {
    Optional<EventDto> findById(UUID id);

    List<EventDto> findAllByOwner(String owner);

    EventDto save(EventDto eventDto);

    void deleteById(UUID eventId);

    boolean existsById(UUID eventId);

    List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1);

    void deleteAllByIds(List<UUID> eventIds);

    List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2);

    List<EventDto> findAll();
}
