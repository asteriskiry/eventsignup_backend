/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantService {
    long countAllByEvent(UUID eventId);

    void deleteAllByEventIn(List<UUID> eventIds);

    List<ParticipantDto> findAllByEvent(UUID eventId);

    void deleteAllByEvent(UUID eventId);

    ParticipantDto save(ParticipantDto participantDto);

    Optional<ParticipantDto> findById(UUID participantId);

    void deleteParticipantByEventAndId(UUID eventId, UUID participantId);

    List<ParticipantDto> findAll();
}
