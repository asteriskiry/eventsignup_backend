/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.entity;

import fi.asteriski.eventsignup.dto.FormDto;
import fi.asteriski.eventsignup.dto.FormField;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.Type;

@Entity
@Data
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
}
