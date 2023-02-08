/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.ArchivedEvent;
import fi.asteriski.eventsignup.domain.ArchivedEventResponse;
import fi.asteriski.eventsignup.domain.Event;
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
        ArchivedEvent archivedEvent = new ArchivedEvent(oldEvent, Instant.now(), numberOfParticipants, oldEvent.getOwner());
        archivedEvent = archivedEventRepository.save(archivedEvent);
        eventService.removeEventAndParticipants(eventId);
        return archivedEvent;
    }

    public void archivePastEvents() {
        Instant now = Instant.now();
        Instant dateLimit = now.minus(defaultDaysToArchivePastEvents, ChronoUnit.DAYS);
        List<Event> events = eventRepository.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit);
        log.info(String.format("Archiving %s events that had startDate or endDate %s days ago i.e. on %s.", events.size(), defaultDaysToArchivePastEvents, dateLimit));
        List<String> eventIds = events.stream().map(Event::getId).collect(Collectors.toCollection(LinkedList::new));
        // TODO delete banner img. or move it to another directory and fix path in event (or add path to ArchivedEvent)
        eventRepository.deleteAllById(eventIds);
        archivedEventRepository.saveAll(events.stream()
            .map(event -> {
                long numberOfParticipants = participantRepository.countAllByEvent(event.getId());
                return new ArchivedEvent(event, now, numberOfParticipants, event.getOwner());
            }).collect(Collectors.toCollection(LinkedList::new)));
        participantRepository.deleteAllByEventIn(eventIds);
    }

    List<ArchivedEventResponse> getAllArchivedEvents() {
        var archivedEvents = archivedEventRepository.findAll();
        return archivedEvents.parallelStream()
            .map(archivedEvent -> {
                var events = archivedEvents.parallelStream()
                    .filter(archivedEvent1 -> archivedEvent.getOriginalOwner().equals(archivedEvent1.getOriginalOwner()))
                    .collect(Collectors.toCollection(LinkedList::new));
                return new ArchivedEventResponse(archivedEvent.getOriginalOwner(), events);
            }).collect(Collectors.toCollection(LinkedList::new));
    }

    List<ArchivedEvent> getAllArchivedEventsForUser(String userId) {
        return archivedEventRepository.findAllByOriginalOwner(userId).stream()
            .sorted(Comparator.comparing(ArchivedEvent::getDateArchived))
            .collect(Collectors.toList());
    }

    void removeArchivedEventsBeforeDate(Instant dateLimit) {
        archivedEventRepository.deleteAllByDateArchivedIsBefore(dateLimit);
    }

    void removeArchivedEvent(String eventId) {
        archivedEventRepository.deleteById(eventId);
    }

    public void removeArchivedEventsOlderThanOneYear() {
        var dateLimit = Instant.now().minus(1, ChronoUnit.YEARS);
        archivedEventRepository.deleteAllByDateArchivedIsBefore(dateLimit);
    }
}
