package fi.asteriski.eventsignup.components.dao.repository;

import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    List<ParticipantEntity> findAllByFormId(UUID formId);

    @Query("select p.name from ParticipantEntity p where p.form.id = :formId")
    List<String> findNamesByFormId(@Param("formId") UUID formId);
}
