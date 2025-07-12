/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dto;

import fi.asteriski.eventsignup.components.entity.EventEntity;
import fi.asteriski.eventsignup.supporting.validation.EventEndDateIsAfterStartDay;
import fi.asteriski.eventsignup.supporting.validation.SignupEndDateIsAfterStartDay;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventEndDateIsAfterStartDay(message = "{validation.date.eventEndDateMustBeAfterStartDay}")
@SignupEndDateIsAfterStartDay(message = "{validation.date.signupEndDateMustBeAfterStartDay}")
@Builder
public record EventDto(
        UUID id,
        @NotNull UUID formId,
        @NotBlank(message = "{validation.event.name.notBlank}") String name,
        @NotBlank(message = "{validation.event.description.notBlank}") String description,
        @NotBlank(message = "{validation.event.place.notBlank}") String place,
        @NotNull(message = "{validation.event.startDate.notNull}")
                @Future(message = "{validation.event.date.inTheFuture}")
            LocalDateTime startDate,
        LocalDateTime endDate,
        @Positive(message = "{validation.event.number.positive}") Integer minParticipants,
        @Positive(message = "{validation.event.number.positive}") Integer maxParticipants,
        @NotNull @FutureOrPresent(message = "{validation.event.signupDate.inTheFuture") LocalDateTime signupStarts,
        LocalDateTime signupEnds,
        @Positive(message = "{validation.event.number.positive}") Double price,
        String bannerImg,
        Map<String, Object> metaData,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ParticipantDto> participants) {

    public EventEntity toEntity(FormDto form, String user) {
        var formEntity = form.toEntity();
        var event = EventEntity.builder()
                .id(id)
                .owner(user)
                .name(name)
                .description(description)
                .place(place)
                .startDate(startDate)
                .endDate(endDate)
                .minParticipants(minParticipants)
                .maxParticipants(maxParticipants)
                .signupStarts(signupStarts)
                .signupEnds(signupEnds)
                .price(price)
                .bannerImg(bannerImg)
                .metaData(metaData)
                .build();

        formEntity.addEvent(event);

        return event;
    }
}
