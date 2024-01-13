/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.ArchivedEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ArchivedEventRepository extends MongoRepository<ArchivedEventEntity, String> {
    List<ArchivedEventEntity> findAllByOriginalOwner(String owner);
    void deleteAllByDateArchivedIsBefore(Instant dateLimit);
}
