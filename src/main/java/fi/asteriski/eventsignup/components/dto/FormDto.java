/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dto;

import fi.asteriski.eventsignup.components.entity.FormEntity;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FormDto(UUID id, UUID eventId, List<FormField> fields, List<ParticipantDto> participants) {
    public FormEntity toEntity() {
        return FormEntity.builder().id(id).fields(fields).build();
    }
}
