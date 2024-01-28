/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.archiving;

import fi.asteriski.eventsignup.dao.archiving.ArchivedEventDao;
import fi.asteriski.eventsignup.domain.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.domain.archiving.ArchivedEventResponse;
import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.exception.EventNotFoundException;
import fi.asteriski.eventsignup.service.event.EventService;
import fi.asteriski.eventsignup.service.event.ImageService;
import fi.asteriski.eventsignup.service.signup.ParticipantService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;

import static fi.asteriski.eventsignup.utils.Constants.UTC_TIME_ZONE;

@Service
@RequiredArgsConstructor
@Log4j2
public class ArchivedEventService {

    private static final String LOG_PREFIX = "[ArchivedEventService]";

    @Value("${default.days.to.archive.past.events}")
    private Integer defaultDaysToArchivePastEvents;
    @NonNull
    private ParticipantService participantService;
    @NonNull
    private ArchivedEventDao archivedEventDao;
    @NonNull
    private EventService eventService;
    @NonNull
    private ImageService imageService;
    @NonNull
    private MessageSource messageSource;

    public ArchivedEventDto archiveEvent(String eventId, Locale usersLocale) {
        Supplier<EventNotFoundException> errorSupplier = (() -> {
            log.error(String.format("%s Unable to archive event. Old event with id <%s> was not found!", LOG_PREFIX, eventId));
            return new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), eventId));
        });
        var oldEvent = eventService.getEvent(eventId, usersLocale, Optional.of(errorSupplier));
        var numberOfParticipants = participantService.countAllByEvent(eventId);
        var archivedEvent = ArchivedEventDto.builder()
            .id(oldEvent.getId())
            .originalEvent(oldEvent)
            .dateArchived(ZonedDateTime.ofInstant(Instant.now(), UTC_TIME_ZONE))
            .numberOfParticipants(numberOfParticipants)
            .originalOwner(oldEvent.getOwner())
            .build();
        archivedEvent = archivedEventDao.save(archivedEvent);
        eventService.removeEventAndParticipants(eventId);
        return archivedEvent;
    }

    public void archivePastEvents() {
        var now = Instant.now();
        var dateLimit = now.minus(defaultDaysToArchivePastEvents, ChronoUnit.DAYS);
        var events = eventService.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit);
        log.info(String.format("Archiving %s events that had startDate or endDate %s days ago i.e. on %s.", events.size(), defaultDaysToArchivePastEvents, dateLimit));
        var eventIds = events.stream().map(EventDto::getId).toList();
        eventService.deleteAllByIds(eventIds);
        archivedEventDao.saveAll(events.stream()
            .map(event -> {
                long numberOfParticipants = participantService.countAllByEvent(event.getId());
                var newBannerImagePath = imageService.moveBannerImage(event.getBannerImg());
                return ArchivedEventDto.builder()
                    .id(event.getId())
                    .originalEvent(event)
                    .dateArchived(ZonedDateTime.ofInstant(now, UTC_TIME_ZONE))
                    .numberOfParticipants(numberOfParticipants)
                    .originalOwner(event.getOwner())
                    .bannerImage(newBannerImagePath)
                    .build();
            }).toList());
        participantService.deleteAllByEventIn(eventIds);
    }

    public List<ArchivedEventResponse> getAllArchivedEvents() {
        var archivedEvents = archivedEventDao.findAll();
        var eventMap = new HashMap<String, List<ArchivedEventDto>>((int) Math.round(1.2 * archivedEvents.size()), 0.9f);
        for (var archivedEvent : archivedEvents) {
            if (!eventMap.containsKey(archivedEvent.originalOwner())) {
                eventMap.put(archivedEvent.originalOwner(), new LinkedList<>());
            }
            eventMap.get(archivedEvent.originalOwner()).add(archivedEvent);
        }
        return eventMap.entrySet().stream()
            .map(entry -> new ArchivedEventResponse(entry.getKey(), entry.getValue()))
            .toList();
    }

    public List<ArchivedEventDto> getAllArchivedEventsForUser(String userId) {
        return archivedEventDao.findAllByOriginalOwner(userId).stream()
            .sorted(Comparator.comparing(ArchivedEventDto::dateArchived))
            .toList();
    }

    public void removeArchivedEventsBeforeDate(Instant dateLimit) {
        archivedEventDao.deleteAllByDateArchivedIsBefore(dateLimit);
    }

    public void removeArchivedEvent(String archivedEventId) {
        archivedEventDao.deleteById(archivedEventId);
    }

    public void removeArchivedEventsOlderThanOneYear() {
        var dateLimit = Instant.now().minus(1, ChronoUnit.YEARS);
        removeArchivedEventsBeforeDate(dateLimit);
    }
}
