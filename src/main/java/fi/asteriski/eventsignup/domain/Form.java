package fi.asteriski.eventsignup.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Map;

@Data
public class Form {

    @Id
    private String id;
    @NonNull
    private final Instant dateCreated;
    @NonNull
    private Map<String, Object> formData;
    @NonNull
    private final String userCreated;
}
