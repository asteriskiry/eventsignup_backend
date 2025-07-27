/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.entity;

import fi.asteriski.eventsignup.components.dto.Answer;
import fi.asteriski.eventsignup.components.dto.ParticipantDto;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

@Data
@Entity
@Table(name = "participants")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NaturalIdCache
public final class ParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String name;

    @Type(JsonType.class)
    @Column(columnDefinition = "json", nullable = false)
    private List<Answer> answers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private FormEntity form;

    @Override
    public int hashCode() {
        return Objects.hash(userEmail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var other = (ParticipantEntity) o;
        return Objects.equals(userEmail, other.userEmail) && Objects.equals(answers, other.answers);
    }

    // Consider moving this toDto mapper to a seperate mapper as it bloats the file
    public ParticipantDto toDto() {
        return ParticipantDto.builder()
                .id(id)
                .userEmail(userEmail)
                .name(name)
                .answers(answers)
                .build();
    }
}
