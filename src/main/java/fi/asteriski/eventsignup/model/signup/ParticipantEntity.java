/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.signup;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "participants",
        indexes = {
            @Index(name = "idx_id_and_event", columnList = "id, event", unique = true),
            @Index(name = "idx_event", columnList = "event")
        })
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull // For lombok
    @NotNull // For openApi
    private String name;

    @NonNull // For lombok
    @NotNull // For openApi
    @Email
    private String email;

    @NonNull // For lombok
    @NotNull // For openApi
    private UUID event;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MealChoice mealChoice;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, String> drinkChoice;

    private String belongsToQuota;
    private Boolean isMember;
    private Boolean hasPaid;
    private Instant signupTime;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> otherData;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> metaData;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;

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
