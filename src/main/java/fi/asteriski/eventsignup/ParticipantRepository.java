/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

import fi.asteriski.eventsignup.domain.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@SuppressWarnings("unused")
public interface ParticipantRepository extends MongoRepository<Participant, String> {

    List<Participant> findAllByEvent(String event);
    long countAllByEvent(String event);
    void deleteAllByEvent(String event);
}
