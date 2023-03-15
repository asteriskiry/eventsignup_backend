/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.ArchivedEvent;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataMongoTest
class ArchivedEventServiceUnitTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    ArchivedEventRepository archivedEventRepository;
    ArchivedEventService archivedEventService;
    @Autowired
    MessageSource messageSource;
    @MockBean
    EventService eventService;
    private final String testUser = "testUser";
    private final Locale defaultLocale = Locale.getDefault();

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        archivedEventService = new ArchivedEventService(eventRepository, participantRepository, archivedEventRepository, eventService, messageSource);
    }

    @AfterEach
    void cleanUp() {
        eventRepository.deleteAll();
        participantRepository.deleteAll();
        archivedEventRepository.deleteAll();
    }

    @Test
    @DisplayName("Archive an existing event with no participants.")
    void archiveExistingEventWithNoParticipants() {
        var event = eventRepository.save(TestUtils.createRandomEvent(testUser).toDto()).toEvent();
        when(eventService.getEvent(eq(event.getId()), eq(defaultLocale), any())).thenReturn(event);
        var result = archivedEventService.archiveEvent(event.getId(), defaultLocale);

        verify(eventService).getEvent(anyString(), any(Locale.class), any());
        assertInstanceOf(ArchivedEvent.class, result);
        assertNotNull(result.getId());
        assertEquals(event, result.getOriginalEvent());
        assertEquals(result.getNumberOfParticipants(), 0L);
    }

    @Test
    @DisplayName("Archive an existing event with no participants.")
    void archiveExistingEventWithParticipants() {
        var event = eventRepository.save(TestUtils.createRandomEvent(testUser).toDto()).toEvent();
        when(eventService.getEvent(eq(event.getId()), eq(defaultLocale), any())).thenReturn(event);
        var participants = TestUtils.getRandomParticipants(event.getId());
        participantRepository.saveAll(participants);
        var result = archivedEventService.archiveEvent(event.getId(), defaultLocale);

        verify(eventService).getEvent(eq(event.getId()), eq(defaultLocale), any());
        assertInstanceOf(ArchivedEvent.class, result);
        assertNotNull(result.getId());
        assertEquals(event, result.getOriginalEvent());
        assertEquals(result.getNumberOfParticipants(), Long.valueOf(participants.size()));
    }

    @Test
    @DisplayName("Try to archive a non existent event.")
    void tryToArchiveNonExistentEvent() {
        when(eventService.getEvent(any(String.class), eq(defaultLocale), any())).thenThrow(new EventNotFoundException("not found"));
        assertThrows(EventNotFoundException.class, () -> archivedEventService.archiveEvent("not-exist", defaultLocale));
    }

    @Test
    void archivePastEvents() {
    }

    @Test
    @DisplayName("Get all archived events when there are some.")
    void getAllArchivedEventsWhenThereAreSome() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser);
        var archivedEvents2 = TestUtils.getRandomArchivedEvents("otherTestUser");
        archivedEventRepository.saveAll(
            Stream.concat(archivedEvents.stream().map(ArchivedEvent::toDto),
                archivedEvents2.stream().map(ArchivedEvent::toDto))
                .toList()
        );
        var result = archivedEventService.getAllArchivedEvents();

        assertInstanceOf(List.class, result);
        assertEquals(result.size(), 2);
        assertTrue(() -> result.stream().allMatch(Objects::nonNull));
        assertEquals(archivedEvents.size(), result.get(0).events().size());
        assertEquals(archivedEvents2.size(), result.get(1).events().size());
    }

    @Test
    @DisplayName("Get all archived events when there are none.")
    void getAllArchivedEventWhenThereAreNone() {
        var result = archivedEventService.getAllArchivedEvents();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Get all archived events for 'testUser'.")
    void getAllArchivedEventsForUser() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser);
        var archivedEvents2 = TestUtils.getRandomArchivedEvents("otherTestUser");
        archivedEventRepository.saveAll(
            Stream.concat(archivedEvents.stream().map(ArchivedEvent::toDto),
                archivedEvents2.stream().map(ArchivedEvent::toDto))
                .toList()
        );
        var result = archivedEventService.getAllArchivedEventsForUser(testUser);

        assertEquals(result.size(), archivedEvents.size());
        assertNotEquals(result.size(), archivedEventRepository.count());
        assertTrue(() -> result.stream().allMatch(archivedEvent -> Objects.equals(archivedEvent.getOriginalOwner(), testUser)));
    }

    @Test
    @DisplayName("Try to remove archived events that were archived more than 100 days ago when there are some to archive.")
    void removeArchivedEventsBeforeDateWhenThereAreSome() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser);
        var now = Instant.now();
        var dateLimit = now.minus(100, ChronoUnit.DAYS);
        for (int i = 0; i < archivedEvents.size(); i++) {
            if (i % 3 == 0) {
                archivedEvents.get(i).setDateArchived(now.minus(120, ChronoUnit.DAYS));
            }
        }
        var numberOfEventsToRemove = archivedEvents.stream()
                .filter(archivedEvent -> archivedEvent.getDateArchived().isBefore(dateLimit))
                .count();
        archivedEventRepository.saveAll(archivedEvents.stream().map(ArchivedEvent::toDto).toList());
        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEventsBeforeDate(dateLimit);
        var countAfter = archivedEventRepository.count();

        assertNotEquals(countBefore,  countAfter);
        assertEquals(countBefore - countAfter, numberOfEventsToRemove);
    }

    @Test
    @DisplayName("Try to remove archived events that were archived more than 100 days ago when there none to remove.")
    void removeArchivedEventsBeforeDateWhenThereAreNone() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser);
        var now = Instant.now();
        var dateLimit = now.minus(100, ChronoUnit.DAYS);
        for (var archivedEvent : archivedEvents) {
            archivedEvent.setDateArchived(now.minus(90, ChronoUnit.DAYS));
        }
        var numberOfEventsToRemove = archivedEvents.stream()
            .filter(archivedEvent -> archivedEvent.getDateArchived().isBefore(dateLimit))
            .count();
        archivedEventRepository.saveAll(archivedEvents.stream().map(ArchivedEvent::toDto).toList());
        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEventsBeforeDate(dateLimit);
        var countAfter = archivedEventRepository.count();

        assertEquals(countBefore, countAfter);
        assertEquals(countBefore - countAfter, numberOfEventsToRemove);
    }

    @Test
    @DisplayName("Remove a specific archived event which exists.")
    void removeArchivedEventThatExists() {
        var archivedEvent = archivedEventRepository.save(TestUtils.createRandomArchivedEvent(testUser).toDto()).toArchivedEvent();

        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEvent(archivedEvent.getId());
        var countAfter = archivedEventRepository.count();

        assertFalse(() -> archivedEventRepository.existsById(archivedEvent.getId()));
        assertNotEquals(countBefore, countAfter);
    }

    @Test
    @DisplayName("Remove a specific archived event that doesn't exist.")
    void removeNonExistentArchivedEvent() {
        archivedEventRepository.save(TestUtils.createRandomArchivedEvent(testUser).toDto());
        var eventIdToTest = "123";
        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEvent(eventIdToTest);
        var countAfter = archivedEventRepository.count();

        assertEquals(countBefore, countAfter);
    }
}
