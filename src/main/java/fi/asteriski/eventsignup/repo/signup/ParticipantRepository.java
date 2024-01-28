/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.repo.signup;

import fi.asteriski.eventsignup.model.signup.ParticipantEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParticipantRepository extends MongoRepository<ParticipantEntity, String> {

    List<ParticipantEntity> findAllByEvent(String event);

    long countAllByEvent(String event);

    void deleteAllByEvent(String event);

    void deleteParticipantByEventAndId(String event, String participant);

    void deleteAllByEventIn(List<String> events);
}
