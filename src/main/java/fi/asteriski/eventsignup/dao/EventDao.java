/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.dao;

import fi.asteriski.eventsignup.dao.entity.EventEntity;
import fi.asteriski.eventsignup.dao.repository.EventRepository;
import fi.asteriski.eventsignup.dto.EventDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.asteriski.eventsignup.utils.Constants.*;

@Component
@AllArgsConstructor
public class EventDao {
    private static final Sort SORT_BY_START_DATE_DESC = Sort.by(Sort.Direction.DESC, "startDate");

    private final EventRepository eventRepository;

    public Optional<EventDto> fetchEventById(@NotNull final UUID eventId) {
        return eventRepository.findById(eventId).map(EventEntity::toDto);
    }

    public void save(@NotNull final EventEntity event) {
        eventRepository.save(event);
    }

    public void createNewEvent(@NotNull final EventDto eventDto) {
        save(eventDto.toEntity());
    }

    public List<EventEntity> fetchNewestEvents() {
        return eventRepository.findAllByCreatedAtIsBefore(
                ZonedDateTime.now().plusDays(1), SORT_BY_CREATED_AT_DESC, MAX_FETCHED_EVENTS);
    }

    public List<EventEntity> fetchUpcomingEvents() {
        var now = ZonedDateTime.now();
        return eventRepository.findAllByEndDateBetween(
                now, now.plusDays(90), SORT_BY_START_DATE_DESC, MAX_FETCHED_EVENTS);
    }

    public List<EventEntity> fetchPastEvents() {
        var now = ZonedDateTime.now();
        return eventRepository.findAllByEndDateBetween(
                now, now.minusDays(90), SORT_BY_START_DATE_DESC, MAX_FETCHED_EVENTS);
    }

    public void updateEvent(@NotNull final EventDto eventDto) {
        var oldEvent = fetchEventEntity(eventDto.id()).orElseThrow(EVENT_NOT_FOUND_EXCEPTION_SUPPLIER);
        oldEvent.update(eventDto);
        save(oldEvent);
    }

    public List<EventDto> fetchUsersEvents(@NotNull final String userName) {
        return eventRepository.findAllByOwner(userName).stream()
                .map(EventEntity::toDto)
                .toList();
    }

    public Optional<EventEntity> fetchEventEntity(@NotNull final UUID eventId) {
        return eventRepository.findById(eventId);
    }
}
