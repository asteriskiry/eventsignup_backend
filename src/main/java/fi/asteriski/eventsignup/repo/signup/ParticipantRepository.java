/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.repo.signup;

import fi.asteriski.eventsignup.domain.signup.ParticipantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParticipantRepository extends MongoRepository<ParticipantEntity, String> {

    List<ParticipantEntity> findAllByEvent(String event);
    long countAllByEvent(String event);
    void deleteAllByEvent(String event);
    void deleteParticipantByEventAndId(String event, String participant);
    void deleteAllByEventIn(List<String> events);
}
