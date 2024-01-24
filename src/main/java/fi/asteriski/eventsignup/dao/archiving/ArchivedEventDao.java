/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.archiving;

import fi.asteriski.eventsignup.domain.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.domain.archiving.ArchivedEventEntity;
import fi.asteriski.eventsignup.repo.archiving.ArchivedEventRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArchivedEventDao {
    @NonNull
    private ArchivedEventRepository archivedEventRepository;

    public ArchivedEventDto save(@NotNull final ArchivedEventDto toSave) {
        return archivedEventRepository.save(toSave.toEntity()).toDto();
    }

    public List<ArchivedEventDto> saveAll(@NotNull final List<ArchivedEventDto> toSave) {
        var items = toSave.stream()
            .map(ArchivedEventDto::toEntity)
            .toList();
        return archivedEventRepository.saveAll(items).stream()
            .map(ArchivedEventEntity::toDto)
            .toList();
    }

    public List<ArchivedEventDto> findAll() {
        return archivedEventRepository.findAll().stream()
            .map(ArchivedEventEntity::toDto)
            .toList();
    }

    public List<ArchivedEventDto> findAllByOriginalOwner(@NotNull final String userId) {
        return archivedEventRepository.findAllByOriginalOwner(userId).stream()
            .map(ArchivedEventEntity::toDto)
            .toList();
    }

    public void deleteAllByDateArchivedIsBefore(@NotNull final Instant dateLimit) {
        archivedEventRepository.deleteAllByDateArchivedIsBefore(dateLimit);
    }

    public void deleteById(@NotNull final String archivedEventId) {
        archivedEventRepository.deleteById(archivedEventId);
    }
}
