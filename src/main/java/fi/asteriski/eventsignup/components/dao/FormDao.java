/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dao;

import static fi.asteriski.eventsignup.supporting.utils.Constants.EVENT_NOT_FOUND_EXCEPTION_SUPPLIER;
import static fi.asteriski.eventsignup.supporting.utils.Constants.FORM_NOT_FOUND_EXCEPTION_SUPPLIER;

import fi.asteriski.eventsignup.components.dao.repository.FormRepository;
import fi.asteriski.eventsignup.components.dto.FormDto;
import fi.asteriski.eventsignup.components.dto.ParticipantDto;
import fi.asteriski.eventsignup.components.entity.EventEntity;
import fi.asteriski.eventsignup.components.entity.FormEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FormDao {
    private final EventDao eventService;
    private final FormRepository formRepository;

    // According to convention
    FormEntity fetchFormEntity(UUID formId) {
        return formRepository.findById(formId).orElseThrow(FORM_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    public List<FormDto> fetchForms(@NotNull List<UUID> formIds) {
        return formRepository.findAllById(formIds).stream()
                .map(FormEntity::toDto)
                .toList();
    }

    public List<FormDto> fetchFormsByEventIds(@NotNull List<UUID> eventIds) {
        return formRepository.findAllByEvent_IdIn(eventIds).stream()
                .map(FormEntity::toDto)
                .toList();
    }

    public Optional<FormDto> fetchForm(@NotNull UUID formId) {
        return formRepository.findById(formId).map(FormEntity::toDto);
    }

    public void createForm(@NotNull final FormDto formDto) {
        save(formDto.toEntity());
    }

    public FormDto updateForm(@NotNull final FormDto formDto) {
        var oldForm = formRepository.findById(formDto.id()).orElseThrow(FORM_NOT_FOUND_EXCEPTION_SUPPLIER);
        EventEntity event = getEventEntity(formDto, oldForm);
        oldForm.update(formDto, event);
        FormEntity saved = formRepository.save(oldForm);
        return saved.toDto();
    }

    private void save(FormEntity entity) {
        formRepository.save(entity);
    }

    private EventEntity getEventEntity(@NotNull final FormDto formDto, @NotNull final FormEntity oldForm) {
        EventEntity event = null;
        if (!oldForm.getEvent().getId().equals(formDto.eventId())) {
            return eventService.fetchEventEntity(formDto.eventId()).orElseThrow(EVENT_NOT_FOUND_EXCEPTION_SUPPLIER);
        }
        return oldForm.getEvent();
    }

}
