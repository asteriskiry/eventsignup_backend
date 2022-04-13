/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@CompoundIndexes({
    @CompoundIndex(name = "id_event", def = "{'id' : 1, 'event': 1}")
})
@Document
@Data
public class Participant {

    @Id
    private String id;
    @NonNull // For lombok
    @NotNull // For openApi
    private final String name;
    @NonNull // For lombok
    @NotNull // For openApi
    @Email
    private String email;
    @NonNull // For lombok
    @NotNull // For openApi
    @Indexed
    private final String event;
    private Gender gender;
    private MealChoice mealChoice;
    private Map<String, String> drinkChoice;
    private String belongsToQuota;
    private Boolean isMember;
    private Boolean hasPaid;
    private Instant signupTime;
    private Map<String, Object> otherData;
    private Map<String, Object> metaData;

}
