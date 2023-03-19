package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.utils.TestUtils;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
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

    private EventService eventService;
    private EventController eventController;
    private Event event;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        eventController = new EventController(eventService);
        event = TestUtils.createRandomEvent(null);
    }

    @Test
    @DisplayName("Get an existing event.")
    void getExistingEvent() {
        when(eventService.getEvent(eq("123"), any(Locale.class), any())).thenReturn(this.event);
        assertInstanceOf(Event.class, eventController.getEvent("123", Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    @DisplayName("Getting an existing event doesn't throw an exception")
    void getExistingEventWithNoExceptionThrown() {
        when(eventService.getEvent(eq("123"), any(Locale.class), any())).thenReturn(this.event);
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
        List<Event> events = new LinkedList<>();
        events.add(this.event);
        when(eventService.getAllEventsForUser(user)).thenReturn(events);
        assertFalse(eventController.getAllEventsForUser(user).isEmpty());
    }

    @Test
    @DisplayName("Get a non-empty list of participants for an event.")
    void getParticipantsForEventWithParticipants() {
        List<Participant> participants = new LinkedList<>();
        participants.add(new Participant("John Doe", "john@example.com", "123"));
        when(eventService.getParticipants("123")).thenReturn(participants);
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
