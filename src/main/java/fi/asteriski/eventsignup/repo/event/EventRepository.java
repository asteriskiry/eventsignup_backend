/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.repo.event;

import fi.asteriski.eventsignup.model.event.EventEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<EventEntity, String> {
    List<EventEntity> findAllByOwner(String owner);

    List<EventEntity> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant instant, Instant instant2);

    List<EventEntity> findAllByStartDateIsBetween(Instant start, Instant end);
}
