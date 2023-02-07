/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Document("event")
@Data
@Builder
public final class EventDto {

    private static final String USE_UTC_TIME_ZONE = "Z";

    @Id
    private String id;
    @NonNull // For lombok
    @NotNull // For openApi
    private String name;
    @NonNull // For lombok
    @NotNull // For openApi
    private Instant startDate;
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
    private Instant endDate;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Instant signupStarts;
    private Instant signupEnds;
    private List<Quota> quotas;
    private Double price;
    private String bannerImg;
    private Map<String, Object> otherData;
    private Map<String, Object> metaData;

    public Event toEvent() {
        var event = Event.builder()
            .id(this.id)
            .name(this.name)
            .startDate(ZonedDateTime.ofInstant(this.startDate, ZoneId.of(USE_UTC_TIME_ZONE)))
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

        if (this.endDate != null) {
            event.setEndDate(ZonedDateTime.ofInstant(this.endDate, ZoneId.of(USE_UTC_TIME_ZONE)));
        }
        if (this.signupStarts != null) {
            event.setSignupStarts(ZonedDateTime.ofInstant(this.signupStarts, ZoneId.of(USE_UTC_TIME_ZONE)));
        }
        if (this.signupEnds != null) {
            event.setSignupEnds(ZonedDateTime.ofInstant(this.signupEnds, ZoneId.of(USE_UTC_TIME_ZONE)));
        }

        return event;
    }
}
