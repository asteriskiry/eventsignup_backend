/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.ArchivedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArchivedEventRepository extends MongoRepository<ArchivedEvent, String> {
}
