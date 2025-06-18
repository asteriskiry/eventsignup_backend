/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao;

import static fi.asteriski.eventsignup.utils.Constants.FORM_NOT_FOUND_EXCEPTION_SUPPLIER;

import fi.asteriski.eventsignup.dao.entity.EventEntity;
import fi.asteriski.eventsignup.dao.entity.FormEntity;
import fi.asteriski.eventsignup.dao.repository.FormRepository;
import fi.asteriski.eventsignup.dto.FormDto;
import fi.asteriski.eventsignup.service.event.EventService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FormDao {
    private final EventService eventService;
    private final FormRepository formRepository;

    public List<FormDto> fetchForms(@NotNull List<UUID> formIds) {
        return formRepository.findAllById(formIds).stream()
                .map(FormEntity::toDto)
                .toList();
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

    private void save(FormEntity entity) {
        formRepository.save(entity);
    }

    private EventEntity getEventEntity(@NotNull final FormDto formDto, @NotNull final FormEntity oldForm) {
        EventEntity event = null;
        if (!oldForm.getEvent().getId().equals(formDto.eventId())) {
            event = eventService.fetchEventForSignupById(formDto.eventId());
        }
        return event;
    }
}
