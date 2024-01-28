/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */

package fi.asteriski.eventsignup.dao.event;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.event.EventEntity;
import fi.asteriski.eventsignup.repo.event.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class EventDaoImpl implements EventDao {

    private EventRepository eventRepository;

    @Override
    public Optional<EventDto> findById(@NotNull final String id) {
        return eventRepository.findById(id)
            .map(EventEntity::toDto);
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
    public void deleteById(@NotNull final String eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public boolean existsById(@NotNull final String eventId) {
        return eventRepository.existsById(eventId);
    }

    @Override
    public List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1) {
        return eventRepository.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit1).stream()
            .map(EventEntity::toDto)
            .toList();
    }

    @Override
    public void deleteAllByIds(@NotNull final List<String> eventIds) {
        eventRepository.deleteAllById(eventIds);
    }

    @Override
    public List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2) {
        return eventRepository.findAllByStartDateIsBetween(date1, date2).stream()
            .map(EventEntity::toDto)
            .toList();
    }
}
