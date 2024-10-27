/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.repo.signup;

import fi.asteriski.eventsignup.model.signup.ParticipantEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {

    List<ParticipantEntity> findAllByEvent(@NotNull UUID event);

    long countAllByEvent(@NotNull UUID event);

    void deleteAllByEvent(@NotNull UUID event);

    void deleteParticipantByEventAndId(UUID event, UUID participant);

    void deleteAllByEventIn(List<UUID> events);
}
