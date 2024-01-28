/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.signup;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ParticipantDto {
    private final String id;
    private final String name;
    private final String email;
    private final String event;
    private final Gender gender;
    private final MealChoice mealChoice;
    private final Map<String, String> drinkChoice;
    private final String belongsToQuota;
    private final Boolean isMember;
    private final Boolean hasPaid;
    private Instant signupTime;
    private final Map<String, Object> otherData;
    private final Map<String, Object> metaData;

    public ParticipantEntity toEntity() {
        return ParticipantEntity.builder()
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
