/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.dao;

import fi.asteriski.eventsignup.dao.entity.EventEntity;
import fi.asteriski.eventsignup.dto.EventDto;
import fi.asteriski.eventsignup.repo.event.EventRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventDao {

    private final EventRepository eventRepository;

    public Optional<EventDto> fetchEventById(@NotNull final UUID eventId) {
        return null;
    }

    public void save(@NotNull final EventEntity event) {}

    public void createNewEvent(@NotNull final EventDto eventDto) {}

    public List<EventEntity> fetchNewestEvents() {
        return null;
    }

    public List<EventEntity> fetchUpcomingEvents() {
        return null;
    }

    public List<EventEntity> fetchPastEvents() {
        return null;
    }

    public void updateEvent(@NotNull final EventDto eventDto) {}

    public List<EventDto> fetchUsersEvents(@NotNull final String userName) {
        return null;
    }

    public Optional<EventEntity> fetchEventForSignup(@NotNull final UUID eventId) {
        return null;
    }

    //    @Override
    //    public Optional<EventDto> findById(@NotNull final UUID id) {
    //        return eventRepository.findById(id).map(EventEntity::toDto);
    //    }
    //
    //    @Override
    //    public List<EventDto> findAllByOwner(@NotNull final String owner) {
    //        return eventRepository.findAllByOwner(owner).stream()
    //                .map(EventEntity::toDto)
    //                .toList();
    //    }
    //
    //    @Override
    //    public EventDto save(@NotNull final EventDto eventDto) {
    //        return eventRepository.save(eventDto.toEntity()).toDto();
    //    }
    //
    //    @Override
    //    public void deleteById(@NotNull final UUID eventId) {
    //        eventRepository.deleteById(eventId);
    //    }
    //
    //    @Override
    //    public boolean existsById(@NotNull final UUID eventId) {
    //        return eventRepository.existsById(eventId);
    //    }
    //
    //    @Override
    //    public List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1) {
    //        return eventRepository.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit1).stream()
    //                .map(EventEntity::toDto)
    //                .toList();
    //    }
    //
    //    @Override
    //    public void deleteAllByIds(@NotNull final List<UUID> eventIds) {
    //        eventRepository.deleteAllById(eventIds);
    //    }
    //
    //    @Override
    //    public List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2) {
    //        return eventRepository.findAllByStartDateIsBetween(date1, date2).stream()
    //                .map(EventEntity::toDto)
    //                .toList();
    //    }
    //
    //    @Override
    //    public List<EventDto> findAll() {
    //        return eventRepository.findAll().stream().map(EventEntity::toDto).toList();
    //    }
}
