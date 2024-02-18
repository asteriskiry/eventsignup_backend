/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.EventDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends MongoRepository<EventDto, String> {
    List<Event> findAllByOwner(String owner);
    List<Event> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant instant, Instant instant2);
    List<EventDto> findAllByStartDateIsBetween(Instant start, Instant end);

}
