
package fi.asteriski.eventsignup.components.dao.repository;

import java.util.List;
import java.util.UUID;

import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    List<ParticipantEntity> findAllByFormId(UUID formId);
}
