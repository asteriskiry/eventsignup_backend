/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.components.dao;

import fi.asteriski.eventsignup.components.dao.repository.EventRepository;
import fi.asteriski.eventsignup.components.dto.EventDto;
import fi.asteriski.eventsignup.components.entity.EventEntity;
import fi.asteriski.eventsignup.supporting.utils.Utils;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.asteriski.eventsignup.supporting.utils.Constants.EVENT_NOT_FOUND_EXCEPTION_SUPPLIER;
import static fi.asteriski.eventsignup.supporting.utils.Constants.MAX_FETCHED_EVENTS;
import static fi.asteriski.eventsignup.supporting.utils.Constants.SORT_BY_CREATED_AT_DESC;

@Component
@AllArgsConstructor
public class EventDao {
    private static final Sort SORT_BY_END_DATE_DESC = Sort.by(Sort.Direction.DESC, "endDate");
    private static final Sort SORT_BY_END_DATE_ASC = Sort.by(Sort.Direction.ASC, "endDate");

    private final EventRepository eventRepository;

    public EventDto createNewEvent(final EventDto requestEnF) {
        var user = Utils.getUserName();
        return save(requestEnF.toEntity(requestEnF.form().getFirst(), user)).toDto();
    }

    public Optional<EventDto> fetchEventById(@NotNull final UUID eventId) {
        return eventRepository.findById(eventId).map(EventEntity::toDto);
    }

    public EventEntity save(@NotNull final EventEntity event) {
        return eventRepository.save(event);
    }

    public List<EventEntity> fetchNewestEvents() {
        return eventRepository.findAllByCreatedAtIsBefore(
                ZonedDateTime.now().toLocalDateTime(), SORT_BY_CREATED_AT_DESC, MAX_FETCHED_EVENTS);
    }

    public List<EventEntity> fetchUpcomingEvents() {
        var now = ZonedDateTime.now();
        return eventRepository.findAllByEndDateBetween(
                now.toLocalDateTime(), now.plusDays(360).toLocalDateTime(), SORT_BY_END_DATE_ASC, MAX_FETCHED_EVENTS);
    }

    public List<EventEntity> fetchPastEvents() {
        var now = ZonedDateTime.now();
        return eventRepository.findAllByEndDateBetween(
                now.minusDays(360).toLocalDateTime(), now.toLocalDateTime(), SORT_BY_END_DATE_DESC, MAX_FETCHED_EVENTS);
    }

    public EventDto updateEvent(@NotNull EventDto eventDto) {
        var existing = fetchEventEntity(eventDto.id()).orElseThrow(EVENT_NOT_FOUND_EXCEPTION_SUPPLIER);
        existing.update(eventDto);
        return eventRepository.save(existing).toDto();
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
