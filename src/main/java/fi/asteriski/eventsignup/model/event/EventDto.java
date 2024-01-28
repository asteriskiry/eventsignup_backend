/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.event;

import fi.asteriski.eventsignup.model.signup.SignupEvent;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class EventDto {
    private String id;
    @NonNull // For lombok
    @NotNull // For openApi
    private final String name;
    @NonNull // For lombok
    @NotNull // For openApi
    private final ZonedDateTime startDate;
    @NonNull // For lombok
    @NotNull // For openApi
    private final String place;
    @NonNull // For lombok
    @NotNull // For openApi
    private final String description;
    @NonNull // For lombok
    @NotNull // For openApi
    private Form form;
    private String owner;
    private final ZonedDateTime endDate;
    private final Integer minParticipants;
    private final Integer maxParticipants;
    private final ZonedDateTime signupStarts;
    private final ZonedDateTime signupEnds;
    private final List<Quota> quotas;
    private final Double price;
    private String bannerImg;
    private final Map<String, Object> otherData;
    private final Map<String, Object> metaData;

    public EventEntity toEntity() {
        var event = EventEntity.builder()
            .id(id)
            .name(name)
            .startDate(startDate.toInstant())
            .place(place)
            .description(description)
            .form(form)
            .owner(owner)
            .minParticipants(minParticipants)
            .maxParticipants(maxParticipants)
            .quotas(quotas)
            .price(price)
            .bannerImg(bannerImg)
            .otherData(otherData)
            .metaData(metaData)
            .build();

        if (signupStarts != null) {
            event.setEndDate(signupStarts.toInstant());
        }
        if (endDate != null) {
            event.setEndDate(endDate.toInstant());
        }
        if (signupEnds != null) {
            event.setSignupEnds(signupEnds.toInstant());
        }

        return event;
    }

    public SignupEvent toSignupEvent() {
        return SignupEvent.builder()
            .id(id)
            .name(name)
            .startDate(startDate)
            .place(place)
            .description(description)
            .form(form)
            .endDate(endDate)
            .price(price)
            .bannerImg(bannerImg)
            .build();
    }
}
