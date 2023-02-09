/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("archivedEvent")
@Data
public class ArchivedEventDto {
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

    public ArchivedEvent toArchivedEvent() {
        return new ArchivedEvent(id, originalEvent.toEvent(), dateArchived, numberOfParticipants, originalOwner);
    }
}
