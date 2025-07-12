/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.supporting.repo.signupDISABLED;

import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface ParticipantRepositoryDISABLED extends JpaRepository<ParticipantEntity, UUID> {

    //    List<ParticipantEntity> findAllByEvent(@NotNull UUID event);
    //
    //    long countAllByEvent(@NotNull UUID event);
    //
    //    void deleteAllByEvent(@NotNull UUID event);
    //
    //    void deleteParticipantByEventAndId(UUID event, UUID participant);
    //
    //    void deleteAllByEventIn(List<UUID> events);
}
