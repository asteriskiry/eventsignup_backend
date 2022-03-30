/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
public class ArchivedEvent {

    @Id
    private String id;
    @NonNull
    private final Event originalEvent;
    @NonNull
    private final Instant dateArchived;
    @NonNull
    private final Long numberOfParticipants;

}
