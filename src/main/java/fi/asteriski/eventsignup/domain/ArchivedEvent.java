/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import fi.asteriski.eventsignup.Constants;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Document()
@Data
@Builder
public class ArchivedEvent {

    @Id
    private String id;
    @NonNull
    private final EventDto originalEvent;
    @NonNull
    private final Instant dateArchived;
    @NonNull
    private final Long numberOfParticipants;
    @NonNull
    @Indexed
    private final String originalOwner;

    public ArchivedEventDto toDto() {
        return ArchivedEventDto.builder()
            .id(id)
            .originalEvent(originalEvent)
            .dateArchived(ZonedDateTime.ofInstant(this.dateArchived, ZoneId.of(Constants.USE_UTC_TIME_ZONE)))
            .numberOfParticipants(numberOfParticipants)
            .originalOwner(originalOwner)
            .build();
    }
}
