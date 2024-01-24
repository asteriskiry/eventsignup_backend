/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain.event;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static fi.asteriski.eventsignup.Constants.UTC_TIME_ZONE;

@Document("event")
@Data
@Builder
public final class EventEntity {

    @Id
    private String id;
    @NonNull
    private String name;
    @NonNull
    private Instant startDate;
    @NonNull
    private String place;
    @NonNull
    private String description;
    @NonNull
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

    public EventDto toDto() {

        return EventDto.builder()
            .id(id)
            .name(name)
            .startDate(ZonedDateTime.ofInstant(startDate, UTC_TIME_ZONE))
            .endDate(endDate != null ? ZonedDateTime.ofInstant(endDate, UTC_TIME_ZONE) : null)
            .signupStarts(signupStarts != null ? ZonedDateTime.ofInstant(signupStarts, UTC_TIME_ZONE) : null)
            .signupEnds(signupEnds != null ? ZonedDateTime.ofInstant(signupStarts, UTC_TIME_ZONE) : null)
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
    }
}
