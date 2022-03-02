package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Map;

@Data
public class Participant {

    @Id
    @NonNull
    private String id;
    @NonNull
    private final String name;
    @NonNull
    private String email;
    @NonNull
    private final ObjectId event;
    private Gender gender;
    private MealChoice mealChoice;
    private Map<String, String> drinkChoice;
    private String belongsToQuota;
    private boolean isMember;
    private boolean hasPaid;
    private Instant signupTime;
    private Map<String, Object> otherData;
    private Map<String, Object> metaData;

}
