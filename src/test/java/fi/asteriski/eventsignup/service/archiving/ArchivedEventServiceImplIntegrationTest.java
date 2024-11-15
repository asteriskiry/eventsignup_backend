/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.archiving;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.asteriski.eventsignup.dao.archiving.ArchivedEventDaoImpl;
import fi.asteriski.eventsignup.exception.EventNotFoundException;
import fi.asteriski.eventsignup.model.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.repo.archiving.ArchivedEventRepository;
import fi.asteriski.eventsignup.repo.event.EventRepository;
import fi.asteriski.eventsignup.repo.signup.ParticipantRepository;
import fi.asteriski.eventsignup.service.event.EventServiceImpl;
import fi.asteriski.eventsignup.service.event.ImageServiceImpl;
import fi.asteriski.eventsignup.service.signup.ParticipantServiceImpl;
import fi.asteriski.eventsignup.utils.TestUtils;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

@DataJpaTest
class ArchivedEventServiceImplIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ArchivedEventRepository archivedEventRepository;

    private ArchivedEventServiceImpl archivedEventService;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private EventServiceImpl eventService;

    @MockBean
    private ImageServiceImpl imageService;

    @MockBean
    private ParticipantServiceImpl participantService;

    private final String testUser = "testUser";
    private final Locale defaultLocale = Locale.getDefault();

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventServiceImpl.class);
        var archivedEventDao = new ArchivedEventDaoImpl(archivedEventRepository);
        imageService = Mockito.mock(ImageServiceImpl.class);
        archivedEventService = new ArchivedEventServiceImpl(
                participantService, archivedEventDao, eventService, imageService, messageSource);
    }

    @AfterEach
    void cleanUp() {
        eventRepository.deleteAll();
        participantRepository.deleteAll();
        archivedEventRepository.deleteAll();
    }

    @Test
    void archiveEvent_whenEventExistAndHasNoParticipants_expectArchivedEventDtoWithNoParticipants() {
        var event = eventRepository
                .save(TestUtils.createRandomEvent(testUser).toEntity())
                .toDto();
        mockEventServiceGetEvent(event);
        when(participantService.countAllByEvent(any(UUID.class))).thenReturn(0L);
        //        when(archivedEventDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = archivedEventService.archiveEvent(event.getId(), defaultLocale);

        verify(eventService).getEvent(any(UUID.class), any(Locale.class), any());
        assertInstanceOf(ArchivedEventDto.class, result);
        assertNotNull(result.id());
        assertEquals(event, result.originalEvent());
        assertEquals(0L, result.numberOfParticipants());
    }

    @Test
    void archiveEvent_whenEventExistAndHasParticipants_expectArchivedEventDtoWithParticipants() {
        var event = eventRepository
                .save(TestUtils.createRandomEvent(testUser).toEntity())
                .toDto();
        mockEventServiceGetEvent(event);
        var participants = TestUtils.createRandomParticipants(event.getId());
        participantRepository.saveAll(
                participants.stream().map(ParticipantDto::toEntity).toList());
        //        when(archivedEventDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(participantService.countAllByEvent(any(UUID.class))).thenReturn((long) participants.size());

        var result = archivedEventService.archiveEvent(event.getId(), defaultLocale);

        verify(eventService).getEvent(eq(event.getId()), eq(defaultLocale), any());
        assertInstanceOf(ArchivedEventDto.class, result);
        assertNotNull(result.id());
        assertEquals(event, result.originalEvent());
        assertEquals(result.numberOfParticipants(), participants.size());
    }

    @Test
    void archiveEvent_whenEventDoesNotExist_throwsEventNotFoundException() {
        when(eventService.getEvent(any(UUID.class), eq(defaultLocale), any()))
                .thenThrow(new EventNotFoundException("not found"));

        assertThrows(
                EventNotFoundException.class,
                () -> archivedEventService.archiveEvent(UUID.randomUUID(), defaultLocale));
    }

    @Test
    void getAllArchivedEvents_givenThereAreSomeInDb_expectNonEmptyList() {
        var archivedEvents = TestUtils.getRandomArchivedEvents(testUser, Optional.empty());
        var archivedEvents2 = TestUtils.getRandomArchivedEvents("otherTestUser", Optional.empty());
        var combinedArchivedEvents = Stream.concat(archivedEvents.stream(), archivedEvents2.stream())
                .map(ArchivedEventDto::toEntity)
                .toList();
        archivedEventRepository.saveAll(combinedArchivedEvents);
        //        when(archivedEventDao.findAll()).thenReturn(combinedArchivedEvents);

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
    void removeArchivedEvent_givenThatTheEventDoesNotExist_expectNothingIsRemovedFromDatabase() {
        archivedEventRepository.save(
                TestUtils.createRandomArchivedEvent(testUser, Optional.empty()).toEntity());
        var eventIdToTest = UUID.randomUUID();

        var countBefore = archivedEventRepository.count();
        archivedEventService.removeArchivedEvent(eventIdToTest);
        var countAfter = archivedEventRepository.count();

        assertEquals(countBefore, countAfter);
    }

    private void mockEventServiceGetEvent(EventDto event) {
        when(eventService.getEvent(eq(event.getId()), eq(defaultLocale), any())).thenReturn(event);
    }
}
