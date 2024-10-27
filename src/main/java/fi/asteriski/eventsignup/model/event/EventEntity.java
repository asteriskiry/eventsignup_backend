/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.event;

import static fi.asteriski.eventsignup.utils.Constants.UTC_TIME_ZONE;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "events",
        indexes = {@Index(name = "idx_owner", columnList = "owner")})
public final class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    private Instant startDate;

    @NonNull
    private String place;

    @NonNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @NonNull
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Form form;

    private String owner;

    private Instant endDate;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Instant signupStarts;
    private Instant signupEnds;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<Quota> quotas;

    private Double price;
    private String bannerImg;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> otherData;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> metaData;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;

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
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
