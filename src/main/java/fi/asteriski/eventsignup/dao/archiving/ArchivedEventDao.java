/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.archiving;

import fi.asteriski.eventsignup.model.archiving.ArchivedEventDto;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public interface ArchivedEventDao {
    ArchivedEventDto save(@NotNull ArchivedEventDto toSave);

    List<ArchivedEventDto> saveAll(@NotNull List<ArchivedEventDto> toSave);

    List<ArchivedEventDto> findAll();

    List<ArchivedEventDto> findAllByOriginalOwner(@NotNull String userId);

    void deleteAllByDateArchivedIsBefore(@NotNull Instant dateLimit);

    void deleteById(@NotNull String archivedEventId);
}
