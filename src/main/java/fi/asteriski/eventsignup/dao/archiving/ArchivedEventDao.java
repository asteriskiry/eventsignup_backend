/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.archiving;

import fi.asteriski.eventsignup.model.archiving.ArchivedEventDto;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ArchivedEventDao {
    ArchivedEventDto save(@NotNull ArchivedEventDto toSave);

    List<ArchivedEventDto> saveAll(@NotNull List<ArchivedEventDto> toSave);

    List<ArchivedEventDto> findAll();

    List<ArchivedEventDto> findAllByOriginalOwner(@NotNull String userId);

    void deleteAllByDateArchivedIsBefore(@NotNull Instant dateLimit);

    void deleteById(@NotNull UUID archivedEventId);
}
