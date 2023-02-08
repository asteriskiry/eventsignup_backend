package fi.asteriski.eventsignup.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.asteriski.eventsignup.domain.ArchivedEvent;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.user.UserRepository;
import fi.asteriski.eventsignup.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Random;

import static fi.asteriski.eventsignup.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(controllers = EventController.class)
class EventControllerIntegrationTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private EventService eventService;
    @MockBean
    private ArchivedEventService archivedEventService;
    // These mock beans are needed since they are declared in EventsignupApplication class.
    @MockBean
    UserRepository userRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    UserService userService;
    @Autowired
    private ObjectMapper mapper;
    private Event event;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity()) // enable security for the mock set up
            .build();
        this.event = createRandomEvent(null);
        // This is needed so Java 8 date/time classes are supported by the mapper.
        this.mapper.registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Get existing event.")
    void getEvent() throws Exception {
        var jsonString = mapper.writeValueAsString(event);
        when(eventService.getEvent(eq("123"), any(Locale.class), any())).thenReturn(event);
        performLogin();
        mockMvc.perform(get("/event/get/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(jsonString, true));
        verify(eventService).getEvent(eq("123"), any(Locale.class), any());
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get non-exiting event with id.")
    void getNonExistentEvent() throws Exception {
        when(eventService.getEvent(eq("987"), any(Locale.class), any())).thenThrow(new EventNotFoundException("not found"));
        performLogin();
        mockMvc.perform(get("/event/get/987")).andExpect(status().isNotFound());
        verify(eventService).getEvent(eq("987"), any(Locale.class), any());
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Make a request for an event but provide no id.")
    void requestEventWithNoId() throws Exception {
        when(eventService.getEvent(eq("123"), any(Locale.class), any())).thenThrow(new IllegalArgumentException("not found"));
        performLogin();
        mockMvc.perform(get("/event/get/")).andExpect(status().isNotFound());
        verify(eventService, never()).getEvent(eq("123"), any(Locale.class), any());
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Get all events for a user.")
    void getAllEventsForUser() throws Exception {
        var events = getRandomEvents("testuser");
        var eventsJson = mapper.writeValueAsString(events);
        when(eventService.getAllEventsForUser("testuser")).thenReturn(events);
        performLogin();
        mockMvc.perform(get("/event/all/testuser")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(eventsJson, true));
        verify(eventService).getAllEventsForUser("testuser");
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Get participants to an event that has participants.")
    void getParticipants() throws Exception {
        var participants = getRandomParticipants(null);
        var participantsJson = mapper.writeValueAsString(participants);
        when(eventService.getParticipants("123")).thenReturn(participants);
        performLogin();
        mockMvc.perform(get("/event/participants/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(participantsJson, true));
        verify(eventService).getParticipants("123");
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Create an event.")
    void createEvent() throws Exception {
        var eventAsJsonString = mapper.writeValueAsString(event);
        var valueCapture = ArgumentCaptor.forClass(User.class);
        var valueCapture2 = ArgumentCaptor.forClass(Locale.class);
        var valueCapture3 = ArgumentCaptor.forClass(ZoneId.class);
        when(eventService.createNewEvent(any(Event.class), valueCapture.capture(), valueCapture2.capture(), valueCapture3.capture())).thenReturn(event);
        performLogin();
        mockMvc.perform(post("/event/create")
            .content(eventAsJsonString).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
        verify(eventService).createNewEvent(any(Event.class), any(), any(Locale.class), any(ZoneId.class));
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Send a GET request to /event/create. Expect error.")
    void sendGetToCreateEvent() throws Exception {
        performLogin();
        mockMvc.perform(get("/event/create"))
            .andExpect(status().isMethodNotAllowed());
    }


    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Edit an existing event.")
    void editEvent() throws Exception {
        var eventAsJsonString = mapper.writeValueAsString(event);
       when(eventService.editExistingEvent(any(Event.class), any(User.class), any(Locale.class), any(ZoneId.class))).thenReturn(event);
       performLogin();
        mockMvc.perform(put("/event/edit")
                .content(eventAsJsonString).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
        verify(eventService).editExistingEvent(any(Event.class), any(), any(Locale.class), any(ZoneId.class));
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Archive an existing event.")
    void archiveExistingEvent() throws Exception {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var archivedEvent = new ArchivedEvent(event, Instant.now(), 10L, "test");
        when(archivedEventService.archiveEvent(valueCapture.capture(), any(Locale.class))).thenReturn(archivedEvent);
        performLogin();
        mockMvc.perform(put("/event/archive/123")).andExpect(status().isOk());
        verify(archivedEventService).archiveEvent(anyString(), any(Locale.class));
        assertEquals("123", valueCapture.getValue());
    }
    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Archive an non-existing event.")
    void archiveNonExistentEvent() throws Exception {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        when(archivedEventService.archiveEvent(valueCapture.capture(), any(Locale.class))).thenThrow(new EventNotFoundException("not found"));
        performLogin();
        mockMvc.perform(put("/event/archive/123")).andExpect(status().isNotFound());
        verify(archivedEventService).archiveEvent(anyString(), any(Locale.class));
        assertEquals("123", valueCapture.getValue());
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Remove an event.")
    void removeEvent() throws Exception {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(eventService).removeEventAndParticipants(valueCapture.capture());
        performLogin();
        mockMvc.perform(delete("/event/remove/123")).andExpect(status().isOk());
        verify(eventService).removeEventAndParticipants("123");
        assertEquals("123", valueCapture.getValue());
    }

    @RepeatedTest(100)
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get events with random ids.")
    void testRandomEventIds() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        when(eventService.getEvent(anyString(), any(Locale.class), any())).thenThrow(new EventNotFoundException("not found"));
        performLogin();
        mockMvc.perform(get(String.format("/event/get/%s", randomString))).andExpect(status().isNotFound());
    }

    @RepeatedTest(100)
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get events with random ids.")
    void testRandomUrls() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        performLogin();
        mockMvc.perform(get(String.format("/%s", randomString))).andExpect(status().isNotFound());
    }

    @RepeatedTest(100)
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get /event/<randomString>")
    void testRandomEventUrls() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        performLogin();
        mockMvc.perform(get(String.format("/event/%s", randomString))).andExpect(status().isNotFound());
    }

    private void performLogin() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().is3xxRedirection());
    }
}
