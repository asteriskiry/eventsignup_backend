/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.archiving;

import fi.asteriski.eventsignup.model.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.model.archiving.ArchivedEventEntity;
import fi.asteriski.eventsignup.repo.archiving.ArchivedEventRepository;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchivedEventDaoImpl implements ArchivedEventDao {
    @NonNull
    private ArchivedEventRepository archivedEventRepository;

    @Override
    public ArchivedEventDto save(@NotNull final ArchivedEventDto toSave) {
        return archivedEventRepository.save(toSave.toEntity()).toDto();
    }

    @Override
    public List<ArchivedEventDto> saveAll(@NotNull final List<ArchivedEventDto> toSave) {
        var items = toSave.stream().map(ArchivedEventDto::toEntity).toList();
        return archivedEventRepository.saveAll(items).stream()
                .map(ArchivedEventEntity::toDto)
                .toList();
    }

    @Override
    public List<ArchivedEventDto> findAll() {
        return archivedEventRepository.findAll().stream()
                .map(ArchivedEventEntity::toDto)
                .toList();
    }

    @Override
    public List<ArchivedEventDto> findAllByOriginalOwner(@NotNull final String userId) {
        return archivedEventRepository.findAllByOriginalOwner(userId).stream()
                .map(ArchivedEventEntity::toDto)
                .toList();
    }

    @Override
    public void deleteAllByDateArchivedIsBefore(@NotNull final Instant dateLimit) {
        archivedEventRepository.deleteAllByDateArchivedIsBefore(dateLimit);
    }

    @Override
    public void deleteById(@NotNull final UUID archivedEventId) {
        archivedEventRepository.deleteById(archivedEventId);
    }
}
