/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dto;

import fi.asteriski.eventsignup.components.entity.FormEntity;
import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ParticipantDto(
        UUID id,
        @NotNull(message = "{validation.signup.eventId.notBlank}") UUID formId,
        @NotBlank(message = "{validation.signup.name.notBlank}") String name,
        @NotBlank(message = "{validation.signup.email.notBlank}") @Email(message = "{validation.signup.email.notValid}")
                String userEmail,
        @Size(min = 1, message = "{validation.signup.answers.size}") List<Answer> answers) {

    //Move to mapper if things get too confusing
    public ParticipantEntity toEntity(FormEntity form) {
        return ParticipantEntity.builder()
            .id(id)
            .name(name)
            .userEmail(userEmail)
            .answers(answers)
            .form(form)
            .build();
    }
}
