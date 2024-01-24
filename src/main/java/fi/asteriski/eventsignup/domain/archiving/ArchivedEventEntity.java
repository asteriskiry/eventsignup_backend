/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain.archiving;

import fi.asteriski.eventsignup.domain.event.EventEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.ZonedDateTime;

import static fi.asteriski.eventsignup.Constants.UTC_TIME_ZONE;

@Document(collection = "ArchivedEvent")
@Data
@Builder
public class ArchivedEventEntity {

    @Id
    private String id;
    @NonNull
    private final EventEntity originalEvent;
    @NonNull
    private final Instant dateArchived;
    @NonNull
    private final Long numberOfParticipants;
    @NonNull
    @Indexed
    private final String originalOwner;
    private final String bannerImage;

    public ArchivedEventDto toDto() {
        return ArchivedEventDto.builder()
            .id(id)
            .originalEvent(originalEvent.toDto())
            .dateArchived(ZonedDateTime.ofInstant(this.dateArchived, UTC_TIME_ZONE))
            .numberOfParticipants(numberOfParticipants)
            .originalOwner(originalOwner)
            .bannerImage(bannerImage)
            .build();
    }
}
