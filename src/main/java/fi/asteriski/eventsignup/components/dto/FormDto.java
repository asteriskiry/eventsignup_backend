/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dto;

import fi.asteriski.eventsignup.components.entity.FormEntity;
import jakarta.validation.constraints.Email;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FormDto(
        UUID id,
        UUID eventId,
        @Email(message = "{validation.signup.email.notValid}") String userEmail,
        List<FormField> fields) {
    public FormEntity toEntity() {
        return FormEntity.builder().id(id).fields(fields).userEmail(userEmail).build();
    }
}
