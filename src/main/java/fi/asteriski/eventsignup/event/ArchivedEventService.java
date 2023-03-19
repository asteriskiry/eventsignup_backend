/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ArchivedEventService {

    private static final String LOG_PREFIX = "[ArchivedEventService]";

    @Value("${default.days.to.archive.past.events}")
    private Integer defaultDaysToArchivePastEvents;
    @NonNull
    private EventRepository eventRepository;
    @NonNull
    private ParticipantRepository participantRepository;
    @NonNull
    private ArchivedEventRepository archivedEventRepository;
    @NonNull
    private EventService eventService;
    @NonNull
    private MessageSource messageSource;

    ArchivedEvent archiveEvent(String eventId, Locale usersLocale) {
        Supplier<EventNotFoundException> errorSupplier = (() -> {
            log.error(String.format("%s Unable to archive event. Old event with id <%s> was not found!", LOG_PREFIX, eventId));
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), eventId));
        });
        Event oldEvent = eventService.getEvent(eventId, usersLocale, Optional.of(errorSupplier));
        long numberOfParticipants = participantRepository.countAllByEvent(eventId);
        ArchivedEventDto archivedEventDto = new ArchivedEventDto(oldEvent.toDto(), Instant.now(), numberOfParticipants, oldEvent.getOwner());
        archivedEventDto = archivedEventRepository.save(archivedEventDto);
        eventService.removeEventAndParticipants(eventId);
        return archivedEventDto.toArchivedEvent();
    }

    public void archivePastEvents() {
        Instant now = Instant.now();
        Instant dateLimit = now.minus(defaultDaysToArchivePastEvents, ChronoUnit.DAYS);
        List<Event> events = eventRepository.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit).stream()
            .map(EventDto::toEvent)
            .toList();
        log.info(String.format("Archiving %s events that had startDate or endDate %s days ago i.e. on %s.", events.size(), defaultDaysToArchivePastEvents, dateLimit));
        List<String> eventIds = events.stream().map(Event::getId).collect(Collectors.toCollection(LinkedList::new));
        // TODO delete banner img. or move it to another directory and fix path in event (or add path to ArchivedEvent)
        eventRepository.deleteAllById(eventIds);
        archivedEventRepository.saveAll(events.stream()
            .map(event -> {
                long numberOfParticipants = participantRepository.countAllByEvent(event.getId());
                return new ArchivedEventDto(event.toDto(), now, numberOfParticipants, event.getOwner());
            }).collect(Collectors.toCollection(LinkedList::new)));
        participantRepository.deleteAllByEventIn(eventIds);
    }

    List<ArchivedEventResponse> getAllArchivedEvents() {
        var archivedEvents = archivedEventRepository.findAll().parallelStream()
            .map(ArchivedEventDto::toArchivedEvent)
            .toList();
        var eventMap = new HashMap<String, List<ArchivedEvent>>((int) Math.round(1.2 *  archivedEvents.size()), 0.9f);
        for (var archivedEvent: archivedEvents) {
            if (!eventMap.containsKey(archivedEvent.getOriginalOwner())) {
                eventMap.put(archivedEvent.getOriginalOwner(), new LinkedList<>());
            }
            eventMap.get(archivedEvent.getOriginalOwner()).add(archivedEvent);
        }
        var returnValue = new LinkedList<ArchivedEventResponse>();
        for (var key: eventMap.keySet()) {
            returnValue.add(new ArchivedEventResponse(key, eventMap.get(key)));
        }
        return returnValue;
    }

    List<ArchivedEvent> getAllArchivedEventsForUser(String userId) {
        return archivedEventRepository.findAllByOriginalOwner(userId).stream()
            .map(ArchivedEventDto::toArchivedEvent)
            .sorted(Comparator.comparing(ArchivedEvent::getDateArchived))
            .collect(Collectors.toList());
    }

    void removeArchivedEventsBeforeDate(Instant dateLimit) {
        archivedEventRepository.deleteAllByDateArchivedIsBefore(dateLimit);
    }

    void removeArchivedEvent(String archivedEventId) {
        archivedEventRepository.deleteById(archivedEventId);
    }

    public void removeArchivedEventsOlderThanOneYear() {
        var dateLimit = Instant.now().minus(1, ChronoUnit.YEARS);
        removeArchivedEventsBeforeDate(dateLimit);
    }
}
