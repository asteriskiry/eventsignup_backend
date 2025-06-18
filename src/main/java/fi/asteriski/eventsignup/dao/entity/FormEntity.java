/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.entity;

import fi.asteriski.eventsignup.dto.FormDto;
import fi.asteriski.eventsignup.dto.FormField;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

@Entity
@Data
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
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Type(JsonType.class)
    @Column(columnDefinition = "json", nullable = false)
    private List<FormField> fields;

    private String userEmail;

    public void addEvent(EventEntity event) {
        this.event = event;
        event.setForm(this);
    }

    public void removeEvent(EventEntity entity) {
        if (Objects.equals(this.event, entity)) {
            this.event.setForm(null);
            this.event = null;
        }
    }

    public FormDto toDto() {
        return FormDto.builder().id(id).eventId(event.getId()).fields(fields).build();
    }

    public void update(@NotNull FormDto formDto, EventEntity event) {
        fields = formDto.fields();
        userEmail = formDto.userEmail();
        if (event != null) {
            removeEvent(getEvent());
            addEvent(event);
        }
    }
}
