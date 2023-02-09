/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;

@AllArgsConstructor
@Data
public class ArchivedEvent {

    private String id;
    @NonNull
    private Event originalEvent;
    @NonNull
    private Instant dateArchived;
    @NonNull
    private Long numberOfParticipants;
    @NonNull
    private String originalOwner;

    public ArchivedEventDto toDto() {
        return new ArchivedEventDto(originalEvent.toDto(), dateArchived, numberOfParticipants, originalOwner);
    }
}
