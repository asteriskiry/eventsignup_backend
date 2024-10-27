/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.dao.event;

import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.event.EventEntity;
import fi.asteriski.eventsignup.repo.event.EventRepository;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventDaoImpl implements EventDao {

    private EventRepository eventRepository;

    @Override
    public Optional<EventDto> findById(@NotNull final UUID id) {
        return eventRepository.findById(id).map(EventEntity::toDto);
    }

    @Override
    public List<EventDto> findAllByOwner(@NotNull final String owner) {
        return eventRepository.findAllByOwner(owner).stream()
                .map(EventEntity::toDto)
                .toList();
    }

    @Override
    public EventDto save(@NotNull final EventDto eventDto) {
        return eventRepository.save(eventDto.toEntity()).toDto();
    }

    @Override
    public void deleteById(@NotNull final UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public boolean existsById(@NotNull final UUID eventId) {
        return eventRepository.existsById(eventId);
    }

    @Override
    public List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1) {
        return eventRepository.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit1).stream()
                .map(EventEntity::toDto)
                .toList();
    }

    @Override
    public void deleteAllByIds(@NotNull final List<UUID> eventIds) {
        eventRepository.deleteAllById(eventIds);
    }

    @Override
    public List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2) {
        return eventRepository.findAllByStartDateIsBetween(date1, date2).stream()
                .map(EventEntity::toDto)
                .toList();
    }

    @Override
    public List<EventDto> findAll() {
        return eventRepository.findAll().stream().map(EventEntity::toDto).toList();
    }
}
