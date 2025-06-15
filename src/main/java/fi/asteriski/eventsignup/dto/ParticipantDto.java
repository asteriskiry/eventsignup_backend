/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ParticipantDto(
        UUID id,
        @NotBlank(message = "{validation.signup.eventId.notBlank}") UUID eventId,
        @NotBlank(message = "{validation.signup.name.notBlank}") @Email(message = "{validation.signup.email.notValid}")
                String userEmail,
        @Size(min = 1, message = "{validation.signup.answers.size}") List<Answer> answers) {}
