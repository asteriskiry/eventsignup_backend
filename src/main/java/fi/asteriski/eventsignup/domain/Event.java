/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Event {

    private String id;
    @NonNull // For lombok
    @NotNull // For openApi
    private String name;
    @NonNull // For lombok
    @NotNull // For openApi
    private ZonedDateTime startDate;
    @NonNull // For lombok
    @NotNull // For openApi
    private String place;
    @NonNull // For lombok
    @NotNull // For openApi
    private String description;
    @NonNull // For lombok
    @NotNull // For openApi
    private Form form;
    @Indexed
    private String owner;
    private ZonedDateTime endDate;
    private Integer minParticipants;
    private Integer maxParticipants;
    private ZonedDateTime signupStarts;
    private ZonedDateTime signupEnds;
    private List<Quota> quotas;
    private Double price;
    private String bannerImg;
    private Map<String, Object> otherData;
    private Map<String, Object> metaData;

    public EventDto toDto() {
        var event = EventDto.builder()
            .id(this.id)
            .name(this.name)
            .startDate(this.startDate.toInstant())
            .place(this.place)
            .description(this.description)
            .form(this.form)
            .owner(this.owner)
            .minParticipants(this.minParticipants)
            .maxParticipants(this.maxParticipants)
            .quotas(this.quotas)
            .price(this.price)
            .bannerImg(this.bannerImg)
            .otherData(this.otherData)
            .metaData(this.metaData)
            .build();

        if (this.signupStarts != null) {
            event.setEndDate(this.signupStarts.toInstant());
        }
        if (this.endDate != null) {
            event.setEndDate(this.endDate.toInstant());
        }
        if (this.signupEnds != null) {
            event.setSignupEnds(this.signupEnds.toInstant());
        }

        return event;
    }
}
