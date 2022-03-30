/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.Map;

@Data
public class Participant {

    @Id
    private String id;
    @NonNull
    private final String name;
    @NonNull
    private String email;
    @NonNull
    @Indexed
    private final String event;
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
