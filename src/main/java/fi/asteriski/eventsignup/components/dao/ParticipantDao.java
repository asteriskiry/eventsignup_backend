/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dao;

import fi.asteriski.eventsignup.components.entity.FormEntity;
import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import fi.asteriski.eventsignup.components.dao.repository.ParticipantRepository;
import fi.asteriski.eventsignup.components.dto.ParticipantDto;
import fi.asteriski.eventsignup.components.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
public class ParticipantDao {

    private final ParticipantRepository participantRepository;
    private final FormService formService;

    public List<ParticipantDto> findAllByFormId(@NotNull UUID formId) {
        return participantRepository
            .findAllByFormId(formId)
            .stream()
            .map(ParticipantEntity::toDto)
            .toList();
    }

    public ParticipantDto save(ParticipantDto dto) {
        // 1. retrieve the form from DB (or get a JPA proxy)
        FormEntity form = formService.fetchForm(dto.formId()).toEntity();

        // 2. convert DTO → Entity
        ParticipantEntity entity = dto.toEntity(form);

        // 3. save and map back to DTO
        ParticipantEntity saved = participantRepository.save(entity);
        return saved.toDto();
    }

    //    @NonNull
    //    private ParticipantRepository participantRepository;
    //
    //    @Override
    //    public long countAllByEvent(@NotNull final UUID eventId) {
    //        return participantRepository.countAllByEvent(eventId);
    //    }
    //
    //    @Override
    //    public void deleteAllByEventIds(@NotNull final List<UUID> eventIds) {
    //        participantRepository.deleteAllByEventIn(eventIds);
    //    }
    //
    //    @Override
    //    public List<ParticipantDto> findAllByEvent(@NotNull final UUID eventId) {
    //        return participantRepository.findAllByEvent(eventId).stream()
    //                .map(ParticipantEntity::toDto)
    //                .toList();
    //    }
    //
    //    @Override
    //    public void deleteAllByEvent(@NotNull final UUID eventId) {
    //        participantRepository.deleteAllByEvent(eventId);
    //    }

    //
    //    @Override
    //    public Optional<ParticipantDto> findById(UUID participantId) {
    //        return participantRepository.findById(participantId).map(ParticipantEntity::toDto);
    //    }
    //
    //    @Override
    //    public void deleteParticipantByEventAndId(UUID eventId, UUID participantId) {
    //        participantRepository.deleteParticipantByEventAndId(eventId, participantId);
    //    }
    //
    //    @Override
    //    public List<ParticipantDto> findAll() {
    //        return participantRepository.findAll().stream()
    //                .map(ParticipantEntity::toDto)
    //                .toList();
    //    }
}
