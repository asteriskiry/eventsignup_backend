package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
public class ArchivedEvent {

    @Id
    @NonNull
    private String id;
    @NonNull
    private final Event originalEvent;
    @NonNull
    private final Instant dateArchived;
    @NonNull
    private final Integer numberOfParticipants;

}
