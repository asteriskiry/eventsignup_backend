/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.signup;

import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantDao {
    long countAllByEvent(@NotNull UUID eventId);

    void deleteAllByEventIds(@NotNull List<UUID> eventIds);

    List<ParticipantDto> findAllByEvent(@NotNull UUID eventId);

    void deleteAllByEvent(@NotNull UUID eventId);

    ParticipantDto save(@NotNull ParticipantDto participantDto);

    Optional<ParticipantDto> findById(UUID participantId);

    void deleteParticipantByEventAndId(UUID eventId, UUID participantId);

    List<ParticipantDto> findAll();
}
