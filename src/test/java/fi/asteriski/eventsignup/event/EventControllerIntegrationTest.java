package fi.asteriski.eventsignup.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.asteriski.eventsignup.domain.ArchivedEvent;
import fi.asteriski.eventsignup.domain.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Random;

import static fi.asteriski.eventsignup.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService eventService;
    @Autowired
    private ObjectMapper mapper;
    private Event event;

    @BeforeEach
    void setUp() {
        this.event = createRandomEvent();
        // This is needed so Java 8 date/time classes are supported by the mapper.
        this.mapper.registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @DisplayName("Get existing event.")
    void getEvent() throws Exception {
        var jsonString = mapper.writeValueAsString(event);
        when(eventService.getEvent("123")).thenReturn(event);
        mockMvc.perform(get("/event/get/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(jsonString, true));
        verify(eventService).getEvent("123");
    }

    @Test
    @DisplayName("Try to get non-exiting event with id.")
    void getNonExistentEvent() throws Exception {
        when(eventService.getEvent("987")).thenThrow(new EventNotFoundException("not found"));
        mockMvc.perform(get("/event/get/987")).andExpect(status().isNotFound());
        verify(eventService).getEvent("987");
    }

    @Test
    @DisplayName("Make a request for an event but provide no id.")
    void requestEventWithNoId() throws Exception {
        when(eventService.getEvent("123")).thenThrow(new IllegalArgumentException("not found"));
        mockMvc.perform(get("/event/get/")).andExpect(status().isNotFound());
        verify(eventService, never()).getEvent("123");
    }

    @Test
    @DisplayName("Get all events for a user.")
    void getAllEventsForUser() throws Exception {
        var events = getRandomEvents();
        var eventsJson = mapper.writeValueAsString(events);
        when(eventService.getAllEventsForUser("testuser")).thenReturn(events);
        mockMvc.perform(get("/event/all/testuser")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(eventsJson, true));
        verify(eventService).getAllEventsForUser("testuser");
    }

    @Test
    @DisplayName("Get participants to an event that has participants.")
    void getParticipants() throws Exception {
        var participants = getRandomParticipants();
        var participantsJson = mapper.writeValueAsString(participants);
        when(eventService.getParticipants("123")).thenReturn(participants);
        mockMvc.perform(get("/event/participants/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(participantsJson, true));
        verify(eventService).getParticipants("123");
    }

    @Test
    @DisplayName("Create an event.")
    void createEvent() throws Exception {
        var eventAsJsonString = mapper.writeValueAsString(event);
        var valueCapture = ArgumentCaptor.forClass(String.class);
        when(eventService.createNewEvent(any(Event.class), valueCapture.capture())).thenReturn(event);
        mockMvc.perform(post("/event/create")
            .content(eventAsJsonString).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
        verify(eventService).createNewEvent(any(Event.class), anyString());
    }

    @Test
    @DisplayName("Send a GET request to /event/create. Expect error.")
    void sendGetToCreateEvent() throws Exception {
        mockMvc.perform(get("/event/create"))
            .andExpect(status().isMethodNotAllowed());
    }


    @Test
    @DisplayName("Edit an existing event.")
    void editEvent() throws Exception {
        var eventAsJsonString = mapper.writeValueAsString(event);
       when(eventService.editExistingEvent(any(Event.class))).thenReturn(event);
        mockMvc.perform(put("/event/edit")
                .content(eventAsJsonString).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
        verify(eventService).editExistingEvent(any(Event.class));
    }

    @Test
    @DisplayName("Archive an existing event.")
    void archiveExistingEvent() throws Exception {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var archivedEvent = new ArchivedEvent(event, Instant.now(), 10L);
        when(eventService.archiveEvent(valueCapture.capture())).thenReturn(archivedEvent);
        mockMvc.perform(put("/event/archive/123")).andExpect(status().isOk());
        verify(eventService).archiveEvent(anyString());
        assertEquals("123", valueCapture.getValue());
    }
    @Test
    @DisplayName("Archive an non-existing event.")
    void archiveNonExistentEvent() throws Exception {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        when(eventService.archiveEvent(valueCapture.capture())).thenThrow(new EventNotFoundException("not found"));
        mockMvc.perform(put("/event/archive/123")).andExpect(status().isNotFound());
        verify(eventService).archiveEvent(anyString());
        assertEquals("123", valueCapture.getValue());
    }

    @Test
    @DisplayName("Remove an event.")
    void removeEvent() throws Exception {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(eventService).removeEventAndParticipants(valueCapture.capture());
        mockMvc.perform(delete("/event/remove/123")).andExpect(status().isOk());
        verify(eventService).removeEventAndParticipants("123");
        assertEquals("123", valueCapture.getValue());
    }

    @RepeatedTest(100)
    @DisplayName("Try to get events with random ids.")
    void testRandomEventIds() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        when(eventService.getEvent(anyString())).thenThrow(new EventNotFoundException("not found"));
        mockMvc.perform(get(String.format("/event/get/%s", randomString))).andExpect(status().isNotFound());
    }

    @RepeatedTest(100)
    @DisplayName("Try to get events with random ids.")
    void testRandomUrls() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        mockMvc.perform(get(String.format("/%s", randomString))).andExpect(status().isNotFound());
    }

    @RepeatedTest(100)
    @DisplayName("Try to get /event/<randomString>")
    void testRandomEventUrls() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        mockMvc.perform(get(String.format("/event/%s", randomString))).andExpect(status().isNotFound());
    }
}
