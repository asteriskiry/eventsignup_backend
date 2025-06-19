/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.entity;

import fi.asteriski.eventsignup.dto.EventDto;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "events",
        indexes = {
            @Index(name = "idx_owner", columnList = "owner"),
            @Index(name = "idx_startDate", columnList = "startDate"),
            @Index(name = "idx_createdAt", columnList = "createdAt")
        })
@NamedEntityGraphs(
        value = {
            @NamedEntityGraph(
                    name = "graph_event_participants",
                    attributeNodes = {@NamedAttributeNode(value = "participants")})
        })
@BatchSize(size = 100)
@Builder
public final class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private FormEntity form;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String place;

    @NonNull
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NonNull
    @Column(nullable = false)
    private ZonedDateTime startDate;

    private ZonedDateTime endDate;
    private Double price;
    private Integer minParticipants;
    private Integer maxParticipants;

    @NonNull
    @Column(nullable = false)
    private ZonedDateTime signupStarts;

    private ZonedDateTime signupEnds;
    private String bannerImg;

    @NonNull
    @Column(nullable = false)
    private String owner;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ParticipantEntity> participants = new LinkedHashSet<>();

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private Map<String, Object> metaData = new LinkedHashMap<>();

    @UpdateTimestamp
    @Column(nullable = false)
    private ZonedDateTime updatedAt;

    @CreationTimestamp
    @Column(nullable = false)
    private ZonedDateTime createdAt;

    public void addParticipant(ParticipantEntity participant) {
        participants.add(participant);
        participant.setEvent(this);
    }

    public void removeParticipant(ParticipantEntity participant) {
        participants.remove(participant);
        participant.setEvent(null);
    }

    public void update(EventDto eventDto) {
        name = eventDto.name();
        place = eventDto.place();
        description = eventDto.description();
        startDate = eventDto.startDate();
        endDate = eventDto.endDate();
        price = eventDto.price();
        minParticipants = eventDto.minParticipants();
        maxParticipants = eventDto.maxParticipants();
        signupStarts = eventDto.signupStarts();
        signupEnds = eventDto.signupEnds();
        bannerImg = eventDto.bannerImg();
    }

    public EventDto toDto() {
        return EventDto.builder()
                .id(id)
                .name(name)
                .place(place)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .price(price)
                .minParticipants(minParticipants)
                .maxParticipants(maxParticipants)
                .signupStarts(signupStarts)
                .signupEnds(signupEnds)
                .bannerImg(bannerImg)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .metaData(metaData)
                .participants(
                        participants.stream().map(ParticipantEntity::toDto).toList())
                .build();
    }
}
