/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.entity;

import fi.asteriski.eventsignup.components.dto.FormDto;
import fi.asteriski.eventsignup.components.dto.FormField;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "forms",
        indexes = {@Index(name = "idx_event_id", columnList = "event_id")})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@BatchSize(size = 100)
public final class FormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ParticipantEntity> participants = new LinkedHashSet<>();

    @Type(JsonType.class)
    @Column(columnDefinition = "json", nullable = false)
    private List<FormField> fields;

    //    public void addEvent(EventEntity event) {
    //        this.event = event;
    //        event.setForm(this);
    //    }
    // JA AI:n ehdotus --------------->
    public void setEvent(EventEntity event) {
        // 1) Detach from any previous event
        if (this.event != null) {
            this.event.getForms().remove(this);
        }
        // 2) Attach to the new event
        this.event = event;
        if (event != null) {
            event.getForms().add(this);
        }
    }
    //
    //    public void removeEvent(EventEntity entity) {
    //        if (Objects.equals(this.event, entity)) {
    //            this.event.setForm(null);
    //            this.event = null;
    //        }
    //    }

    //    @OneToMany(
    //        mappedBy = "form",
    //        cascade = CascadeType.ALL,
    //        orphanRemoval = true,
    //        fetch = FetchType.LAZY
    //    )
    //    public void addParticipant(ParticipantEntity participant) {
    //        participants.add(participant);
    //        participant.setForm(this);
    //    }

    //    public void removeParticipant(ParticipantEntity participant) {
    //        participants.remove(participant);
    //        participant.setForm(null);
    //    }

    public FormDto toDto() {
        return FormDto.builder()
                .id(id)
                .eventId(event.getId())
                .fields(fields)
                .participants(
                        participants.stream().map(ParticipantEntity::toDto).toList())
                .build();
    }

    public void update(@NotNull FormDto formDto, EventEntity event) {
        fields = formDto.fields();
        // Sync parent event:
        setEvent(event);
    }
}
