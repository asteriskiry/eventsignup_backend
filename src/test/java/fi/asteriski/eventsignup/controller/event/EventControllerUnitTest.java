package fi.asteriski.eventsignup.controller.event;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.signup.ParticipantDto;
import fi.asteriski.eventsignup.exception.EventNotFoundException;
import fi.asteriski.eventsignup.service.event.EventServiceImpl;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
        when(eventService.getEvent(eq("123"), any(Locale.class), any())).thenReturn(this.eventDto);
        assertInstanceOf(EventDto.class, eventController.getEvent("123", Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    @DisplayName("Getting an existing event doesn't throw an exception")
    void getExistingEventWithNoExceptionThrown() {
        when(eventService.getEvent(eq("123"), any(Locale.class), any())).thenReturn(this.eventDto);
        assertDoesNotThrow(() -> eventController.getEvent("123", null, null));
    }

    @Test
    @DisplayName("Try to get non-existent event.")
    void tryToGetNonExistentEvent() {
        when(eventService.getEvent(eq("not-exist"), any(Locale.class), any())).thenThrow(new EventNotFoundException("not-exist"));
        assertThrows(EventNotFoundException.class, () -> eventController.getEvent("not-exist", Locale.getDefault(), ZoneId.systemDefault()));
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
        var participantEntities = List.of(ParticipantDto.builder().name("John Doe").email("john@example.com").event("123").build());
        when(eventService.getParticipants("123")).thenReturn(participantEntities);
        assertInstanceOf(List.class, eventController.getParticipants("123"));
        assertFalse(eventController.getParticipants("123").isEmpty());
    }

    @Test
    @DisplayName("Get an empty list of participants for an event.")
    void getParticipantsForEventWithNoParticipants() {
        when(eventService.getParticipants("123")).thenReturn(new LinkedList<>());
        assertInstanceOf(List.class, eventController.getParticipants("123"));
        assertTrue(eventController.getParticipants("123").isEmpty());
    }
}
