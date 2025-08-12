/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.entity;

import fi.asteriski.eventsignup.components.dto.EventDto;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
@NamedEntityGraph(
        name = "event-with-forms-and-participants",
        attributeNodes = {@NamedAttributeNode(value = "forms", subgraph = "forms-subgraph")},
        subgraphs = {
            @NamedSubgraph(
                    name = "forms-subgraph",
                    attributeNodes = {@NamedAttributeNode("participants")})
        })
@BatchSize(size = 100)
@Builder
public final class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<FormEntity> forms = new LinkedHashSet<>();

    // AI:n add ja remove:
    public void addForm(FormEntity form) {
        form.setEvent(this);
    }

    public void removeForm(FormEntity form) {
        form.setEvent(null);
    }

    @NonNull
    @Column(nullable = false)
    private String place;

    @NonNull
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NonNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;
    private Double price;
    private Integer minParticipants;
    private Integer maxParticipants;

    @NonNull
    @Column(nullable = false)
    private LocalDateTime signupStarts;

    private LocalDateTime signupEnds;
    private String bannerImg;

    @NonNull
    @Column(nullable = false)
    private String owner;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private Map<String, Object> metaData = new LinkedHashMap<>();

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void update(EventDto eventDto) {
        name = eventDto.name();
        place = eventDto.place();
        description = eventDto.description();
        owner = eventDto.owner();
        startDate = eventDto.startDate();
        endDate = eventDto.endDate();
        price = eventDto.price();
        minParticipants = eventDto.minParticipants();
        maxParticipants = eventDto.maxParticipants();
        signupStarts = eventDto.signupStarts();
        signupEnds = eventDto.signupEnds();
        bannerImg = eventDto.bannerImg();
    }

    // Note: The mapper is part of entity structure and not in a seperate mapper
    public EventDto toDto() {
        return EventDto.builder()
                .id(id)
                .name(name)
                .place(place)
                .description(description)
                .owner(owner)
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
                .forms(forms.stream().map(FormEntity::toDto).toList())
                .build();
    }
}
