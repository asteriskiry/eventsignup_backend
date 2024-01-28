/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.signup;

import fi.asteriski.eventsignup.model.signup.ParticipantDto;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface ParticipantDao {
    long countAllByEvent(@NotNull String eventId);

    void deleteAllByEventIds(@NotNull List<String> eventIds);

    List<ParticipantDto> findAllByEvent(@NotNull String eventId);

    void deleteAllByEvent(@NotNull String eventId);

    ParticipantDto save(@NotNull ParticipantDto participantDto);

    Optional<ParticipantDto> findById(String participantId);

    void deleteParticipantByEventAndId(String eventId, String participantId);

    List<ParticipantDto> findAll();
}
