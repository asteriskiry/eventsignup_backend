/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dao.repository;

import fi.asteriski.eventsignup.components.entity.EventEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    List<EventEntity> findAllByOwner(String owner);

    @EntityGraph(value = "event-with-forms-and-participants")
    List<EventEntity> findAllByCreatedAtIsBefore(LocalDateTime createdAtBefore, Sort sort, Limit limit);

    @EntityGraph(value = "event-with-forms-and-participants")
    List<EventEntity> findAllByEndDateBetween(LocalDateTime start, LocalDateTime end, Sort sort, Limit limit);
}
