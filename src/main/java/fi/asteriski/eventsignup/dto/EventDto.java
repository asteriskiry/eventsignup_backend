/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dto;

import fi.asteriski.eventsignup.validation.EventEndDateIsAfterStartDay;
import fi.asteriski.eventsignup.validation.SignupEndDateIsAfterStartDay;
import jakarta.validation.constraints.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventEndDateIsAfterStartDay(message = "{validation.date.eventEndDateMustBeAfterStartDay}")
@SignupEndDateIsAfterStartDay(message = "{validation.date.signupEndDateMustBeAfterStartDay}")
public record EventDto(
        UUID id,
        @NotBlank UUID formId,
        @NotBlank(message = "{validation.event.name.notBlank}") String name,
        @NotBlank(message = "{validation.event.description.notBlank}") String description,
        @NotBlank(message = "{validation.event.place.notBlank}") String place,
        @NotNull(message = "{validation.event.startDate.notNull}")
                @Future(message = "{validation.event.date.inTheFuture}")
                ZonedDateTime startDate,
        ZonedDateTime endDate,
        @Positive(message = "{validation.event.number.positive}") Integer minParticipants,
        @Positive(message = "{validation.event.number.positive}") Integer maxParticipants,
        @NotNull @FutureOrPresent(message = "{validation.event.signupDate.inTheFuture") ZonedDateTime signupStarts,
        ZonedDateTime signupEnds,
        @Positive(message = "{validation.event.number.positive}") Double price,
        String bannerImg,
        Map<String, Object> metaData,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        List<ParticipantDto> participants) {}
