/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.repo.archiving;

import fi.asteriski.eventsignup.model.archiving.ArchivedEventEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArchivedEventRepository extends MongoRepository<ArchivedEventEntity, String> {
    List<ArchivedEventEntity> findAllByOriginalOwner(String owner);

    void deleteAllByDateArchivedIsBefore(Instant dateLimit);
}
