package fi.asteriski.eventsignup.controller.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import fi.asteriski.eventsignup.exception.EventNotFoundException;
import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.service.event.EventServiceImpl;
import fi.asteriski.eventsignup.utils.TestUtils;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EventControllerUnitTest {

    private EventServiceImpl eventService;
    private EventController eventController;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventServiceImpl.class);
        eventController = new EventController(eventService);
        eventDto = TestUtils.createRandomEvent(null);
    }

    @Test
    @DisplayName("Get an existing event.")
    void getExistingEvent() {
        var id = UUID.randomUUID();
        when(eventService.getEvent(eq(id), any(Locale.class), any())).thenReturn(this.eventDto);
        assertInstanceOf(EventDto.class, eventController.getEvent(id, Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    @DisplayName("Getting an existing event doesn't throw an exception")
    void getExistingEventWithNoExceptionThrown() {
        var id = UUID.randomUUID();
        when(eventService.getEvent(eq(id), any(Locale.class), any())).thenReturn(this.eventDto);
        assertDoesNotThrow(() -> eventController.getEvent(id, null, null));
    }

    @Test
    @DisplayName("Try to get non-existent event.")
    void tryToGetNonExistentEvent() {
        var id = UUID.randomUUID();
        when(eventService.getEvent(eq(id), any(Locale.class), any()))
                .thenThrow(new EventNotFoundException("not-exist"));
        assertThrows(
                EventNotFoundException.class,
                () -> eventController.getEvent(id, Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    @DisplayName("Get an empty list of event to user with no events.")
    void getAllEventsForUserWithNoEvents() {
        var user = "user";
        when(eventService.getAllEventsForUser(user)).thenReturn(new LinkedList<>());
        assertTrue(eventController.getAllEventsForUser(user).isEmpty());
    }

    @Test
    @DisplayName("Get a non-empty list of events for user with events.")
    void getAllEventsForUserWithEvents() {
        var user = "user";
        List<EventDto> eventDtos = new LinkedList<>();
        eventDtos.add(this.eventDto);
        when(eventService.getAllEventsForUser(user)).thenReturn(eventDtos);
        assertFalse(eventController.getAllEventsForUser(user).isEmpty());
    }

    @Test
    @DisplayName("Get a non-empty list of participants for an event.")
    void getParticipantsForEventWithParticipants() {
        var id = UUID.randomUUID();
        var participantEntities = List.of(ParticipantDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .event(id)
                .build());
        when(eventService.getParticipants(id)).thenReturn(participantEntities);
        assertInstanceOf(List.class, eventController.getParticipants(id));
        assertFalse(eventController.getParticipants(id).isEmpty());
    }

    @Test
    @DisplayName("Get an empty list of participants for an event.")
    void getParticipantsForEventWithNoParticipants() {
        var id = UUID.randomUUID();
        when(eventService.getParticipants(id)).thenReturn(new LinkedList<>());
        assertInstanceOf(List.class, eventController.getParticipants(id));
        assertTrue(eventController.getParticipants(id).isEmpty());
    }
}
