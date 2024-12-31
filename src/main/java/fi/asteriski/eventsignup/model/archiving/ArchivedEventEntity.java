/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.archiving;

import fi.asteriski.eventsignup.model.event.EventEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
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
        name = "archived_events",
        indexes = {@Index(name = "idx_originalOwner", columnList = "originalOwner")})
public class ArchivedEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private EventEntity originalEvent;

    @NonNull
    @CreationTimestamp(source = SourceType.DB)
    private ZonedDateTime dateArchived;

    @NonNull
    private Long numberOfParticipants;

    @NonNull
    private String originalOwner;

    private String bannerImage;

    @UpdateTimestamp(source = SourceType.DB)
    private ZonedDateTime dateUpdated;

    public ArchivedEventDto toDto() {
        return ArchivedEventDto.builder()
                .id(id)
                .originalEvent(originalEvent.toDto())
                .dateArchived(dateArchived)
                .numberOfParticipants(numberOfParticipants)
                .originalOwner(originalOwner)
                .bannerImage(bannerImage)
                .build();
    }
}
