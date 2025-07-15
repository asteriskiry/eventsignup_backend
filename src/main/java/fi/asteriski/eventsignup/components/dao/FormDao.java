/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dao;

import fi.asteriski.eventsignup.components.entity.EventEntity;
import fi.asteriski.eventsignup.components.entity.FormEntity;
import fi.asteriski.eventsignup.components.dao.repository.FormRepository;
import fi.asteriski.eventsignup.components.dto.FormDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.asteriski.eventsignup.supporting.utils.Constants.EVENT_NOT_FOUND_EXCEPTION_SUPPLIER;
import static fi.asteriski.eventsignup.supporting.utils.Constants.FORM_NOT_FOUND_EXCEPTION_SUPPLIER;

@Component
@AllArgsConstructor
public class FormDao {
    private final EventDao eventService;
    private final FormRepository formRepository;

    public List<FormDto> fetchForms(@NotNull List<UUID> formIds) {
        return formRepository.findAllById(formIds).stream()
            .map(FormEntity::toDto)
            .toList();
    }
    public Optional<FormDto> fetchForm(@NotNull UUID formId) {
        return formRepository.findById(formId)
            .map(FormEntity::toDto);
    }

    public void createForm(@NotNull final FormDto formDto) {
        save(formDto.toEntity());
    }

    public void updateForm(@NotNull final FormDto formDto) {
        var oldForm = formRepository.findById(formDto.id()).orElseThrow(FORM_NOT_FOUND_EXCEPTION_SUPPLIER);
        EventEntity event = getEventEntity(formDto, oldForm);
        oldForm.update(formDto, event);
        save(oldForm);
    }

    //According to convention
    FormEntity fetchFormEntity(UUID formId) {
        return formRepository.findById(formId).orElseThrow(FORM_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    private void save(FormEntity entity) {
        formRepository.save(entity);
    }

    private EventEntity getEventEntity(@NotNull final FormDto formDto, @NotNull final FormEntity oldForm) {
        EventEntity event = null;
        if (!oldForm.getEvent().getId().equals(formDto.eventId())) {
            event = eventService.fetchEventEntity(formDto.eventId())
                .orElseThrow(EVENT_NOT_FOUND_EXCEPTION_SUPPLIER);
        }
        return event;
    }
}
