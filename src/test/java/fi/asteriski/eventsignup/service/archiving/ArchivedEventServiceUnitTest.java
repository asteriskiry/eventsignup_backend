/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.archiving;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.EventDto;
import fi.asteriski.eventsignup.domain.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.event.EventNotFoundException;
import fi.asteriski.eventsignup.event.EventRepository;
import fi.asteriski.eventsignup.event.EventService;
import fi.asteriski.eventsignup.repo.archiving.ArchivedEventRepository;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataMongoTest
class ArchivedEventServiceUnitTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private ArchivedEventRepository archivedEventRepository;
    private ArchivedEventService archivedEventService;
    @Autowired
    private MessageSource messageSource;
    @MockBean
    private EventService eventService;
    private final String testUser = "testUser";
    private final Locale defaultLocale = Locale.getDefault();
    private final Supplier<Instant> lessThanHundredDaysAgoSupplier = () -> Instant.now().minus(60, ChronoUnit.DAYS);

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
    void archiveEvent_whenEventExistAndHasNoParticipants_expectArchivedEventDtoWithNoParticipants() {
        var event = eventRepository.save(TestUtils.createRandomEvent(testUser).toDto());
        mockEventServiceGetEvent(event);

        var result = archivedEventService.archiveEvent(event.getId(), defaultLocale);

        verify(eventService).getEvent(anyString(), any(Locale.class), any());
        assertInstanceOf(ArchivedEventDto.class, result);
        assertNotNull(result.id());
        assertEquals(event, result.originalEvent());
        assertEquals(0L, result.numberOfParticipants());
    }

    @Test
    void archiveEvent_whenEventExistAndHasNoParticipants_expectArchivedEventDtoWithParticipants() {
        var event = eventRepository.save(TestUtils.createRandomEvent(testUser).toDto());
        mockEventServiceGetEvent(event);
        var participants = TestUtils.getRandomParticipants(event.getId());
        participantRepository.saveAll(participants);

        var result = archivedEventService.archiveEvent(event.getId(), defaultLocale);

        verify(eventService).getEvent(eq(event.getId()), eq(defaultLocale), any());
        assertInstanceOf(ArchivedEventDto.class, result);
        assertNotNull(result.id());
        assertEquals(event, result.originalEvent());
        assertEquals(result.numberOfParticipants(), participants.size());
    }

    @Test
    void archiveEvent_whenEventDoesNotExist_throwsEventNotFoundException() {
        when(eventService.getEvent(any(String.class), eq(defaultLocale), any())).thenThrow(new EventNotFoundException("not found"));

        assertThrows(EventNotFoundException.class, () -> archivedEventService.archiveEvent("not-exist", defaultLocale));
    }

    @Test
    void getAllArchivedEvents_givenThereAreSomeInDb_expectNonEmptyList() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.empty());
        var archivedEvents2 = TestUtils.getRandomArchivedEvents("otherTestUser", Optional.empty());
        archivedEventRepository.saveAll(
            Stream.concat(archivedEvents.stream(),
                    archivedEvents2.stream())
                .toList()
        );

        var result = archivedEventService.getAllArchivedEvents();

        assertInstanceOf(List.class, result);
        assertEquals(2, result.size());
        assertTrue(() -> result.stream().allMatch(Objects::nonNull));
        assertEquals(archivedEvents.size(), result.get(0).events().size());
        assertEquals(archivedEvents2.size(), result.get(1).events().size());
    }

    @Test
    void getAllArchivedEvents_givenThereIsNothingInDb_expectEmptyList() {
        var result = archivedEventService.getAllArchivedEvents();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllArchivedEventsForUser_givenDbHasArchivedEventsForMultipleUsers_expectAListForJustOneUser() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.empty());
        var archivedEvents2 = TestUtils.getRandomArchivedEvents("otherTestUser", Optional.empty());
        archivedEventRepository.saveAll(
            Stream.concat(archivedEvents.stream(),
                    archivedEvents2.stream())
                .toList()
        );

        var result = archivedEventService.getAllArchivedEventsForUser(testUser);

        assertEquals(result.size(), archivedEvents.size());
        assertNotEquals(result.size(), archivedEventRepository.count());
        assertTrue(() -> result.stream().allMatch(archivedEvent -> Objects.equals(archivedEvent.originalOwner(), testUser)));
    }

    @Test
    void removeArchivedEventsBeforeDate_giveThereAreArchivedEventsBeforeAndAfterTheDate_expectCountIsNotEqualToFullAmount() {
        var now = Instant.now();
        Supplier<Instant> moreThanHundredDaysAgoSupplier = () -> now.minus(120, ChronoUnit.DAYS);
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.of(moreThanHundredDaysAgoSupplier));
        archivedEvents.addAll(TestUtils.getRandomArchivedEvents(testUser, Optional.of(lessThanHundredDaysAgoSupplier)));
        var dateLimit = now.minus(100, ChronoUnit.DAYS);
        var numberOfEventsToRemove = archivedEvents.stream()
            .filter(archivedEventEntity -> archivedEventEntity.getDateArchived().isBefore(dateLimit))
            .count();
        archivedEventRepository.saveAll(archivedEvents);

        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEventsBeforeDate(dateLimit);
        var countAfter = archivedEventRepository.count();

        assertNotEquals(countBefore, countAfter);
        assertEquals(countBefore - countAfter, numberOfEventsToRemove);
    }

    @Test
    void removeArchivedEventsBeforeDate_givenThereAreNoneToRemove_expectNothingIsRemovedFromDatabase() {
        var now = Instant.now();
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.of(lessThanHundredDaysAgoSupplier));
        var dateLimit = now.minus(100, ChronoUnit.DAYS);
        var numberOfEventsToRemove = archivedEvents.stream()
            .filter(archivedEventEntity -> archivedEventEntity.getDateArchived().isBefore(dateLimit))
            .count();
        archivedEventRepository.saveAll(archivedEvents);

        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEventsBeforeDate(dateLimit);
        var countAfter = archivedEventRepository.count();

        assertEquals(countBefore, countAfter);
        assertEquals(countBefore - countAfter, numberOfEventsToRemove);
    }

    @Test
    void removeArchivedEvent_givenThatTheEventExists_expectItToBeDeletedFromDatabase() {
        var archivedEvent = archivedEventRepository.save(TestUtils.createRandomArchivedEvent(testUser, Optional.empty()));

        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEvent(archivedEvent.getId());
        var countAfter = archivedEventRepository.count();

        assertFalse(() -> archivedEventRepository.existsById(archivedEvent.getId()));
        assertNotEquals(countBefore, countAfter);
    }

    @Test
    void removeArchivedEvent_givenThatTheEventDoesNotExist_expectNothingIsRemovedFromDatabase() {
        archivedEventRepository.save(TestUtils.createRandomArchivedEvent(testUser, Optional.empty()));
        var eventIdToTest = "123";

        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEvent(eventIdToTest);
        var countAfter = archivedEventRepository.count();

        assertEquals(countBefore, countAfter);
    }

    private void mockEventServiceGetEvent(EventDto event) {
        when(eventService.getEvent(eq(event.getId()), eq(defaultLocale), any())).thenReturn(event.toEvent());
    }
}
