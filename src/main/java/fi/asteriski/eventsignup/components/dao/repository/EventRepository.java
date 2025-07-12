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
    @EntityGraph("graph_event_participants")
    List<EventEntity> findAllByOwner(String owner);

    List<EventEntity> findAllByCreatedAtIsBefore(LocalDateTime createdAtBefore, Sort sort, Limit limit);

    List<EventEntity> findAllByEndDateBetween(
            LocalDateTime endDateAfter, LocalDateTime endDateBefore, Sort sort, Limit limit);
}
