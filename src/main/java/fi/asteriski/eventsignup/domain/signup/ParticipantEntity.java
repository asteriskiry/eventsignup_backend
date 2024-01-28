/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain.signup;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@CompoundIndex(name = "id_event", def = "{'id' : 1, 'event': 1}")
@Document("Participant")
@Data
@Builder
public class ParticipantEntity {

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

    public ParticipantDto toDto() {
        return ParticipantDto.builder()
            .id(id)
            .name(name)
            .email(email)
            .event(event)
            .gender(gender)
            .mealChoice(mealChoice)
            .drinkChoice(drinkChoice)
            .belongsToQuota(belongsToQuota)
            .isMember(isMember)
            .hasPaid(hasPaid)
            .signupTime(signupTime)
            .otherData(otherData)
            .metaData(metaData)
            .build();
    }
}
