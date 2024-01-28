/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import java.util.List;
import java.util.Optional;

public interface ParticipantService {
    long countAllByEvent(String eventId);

    void deleteAllByEventIn(List<String> eventIds);

    List<ParticipantDto> findAllByEvent(String eventId);

    void deleteAllByEvent(String eventId);

    ParticipantDto save(ParticipantDto participantDto);

    Optional<ParticipantDto> findById(String participantId);

    void deleteParticipantByEventAndId(String eventId, String participantId);

    List<ParticipantDto> findAll();
}
