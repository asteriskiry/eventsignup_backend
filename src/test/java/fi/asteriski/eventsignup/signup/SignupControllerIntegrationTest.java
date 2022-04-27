/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.event.EventNotFoundException;
import fi.asteriski.eventsignup.user.UserRepository;
import fi.asteriski.eventsignup.user.UserService;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZoneId;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignupController.class)
class SignupControllerIntegrationTest {

    private MockMvc mockMvc;
    @MockBean
    private SignupService signupService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private WebApplicationContext context;
    // These mock beans are needed since they are declared in EventsignupApplication class.
    @MockBean
    UserRepository userRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    UserService userService;
    private Participant participant;
    private Event event;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity()) // enable security for the mock set up
            .build();
        this.participant = TestUtils.createRandomParticipant(null);
        this.event = TestUtils.createRandomEvent(null);
        // This is needed so Java 8 date/time classes are supported by the mapper.
        this.mapper.registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @DisplayName("Get an existing event for signup.")
    void getExistingEventForSignup() throws Exception {
        var jsonString = mapper.writeValueAsString(event);
        when(signupService.getEventForSignUp(anyString(), any(Locale.class), any(ZoneId.class))).thenReturn(event);
        mockMvc.perform(get("/signup/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(jsonString, true));
        verify(signupService).getEventForSignUp(eq("123"), any(Locale.class), any(ZoneId.class));
    }

    @Test
    @DisplayName("Get non-existing event for signup.")
    void getNonExistingEventForSignup() throws Exception {
        when(signupService.getEventForSignUp(anyString(), any(Locale.class), any(ZoneId.class))).thenThrow(EventNotFoundException.class);
        mockMvc.perform(get("/signup/456")).andExpect(status().isNotFound());
        verify(signupService).getEventForSignUp(eq("456"), any(Locale.class), any(ZoneId.class));
    }

    @Test
    @DisplayName("Get existing event for signup where event is full.")
        when(signupService.getEventForSignUp(anyString(), any(Locale.class), any(ZoneId.class))).thenThrow(EventFullException.class);
        mockMvc.perform(get("/signup/789")).andExpect(status().isConflict());
        verify(signupService).getEventForSignUp(eq("789"), any(Locale.class), any(ZoneId.class));
    }

    @Test
    @DisplayName("Get existing event for signup where signup hasn't started yet.")
    void getExistingEventForSignupWhereSignupHasNotStarted() throws Exception {
        when(signupService.getEventForSignUp(anyString(), any(Locale.class), any(ZoneId.class))).thenThrow(SignupNotStartedException.class);
        mockMvc.perform(get("/signup/1234")).andExpect(status().isConflict());
        verify(signupService).getEventForSignUp(eq("1234"), any(Locale.class), any(ZoneId.class));
    }

    @Test
    @DisplayName("Get existing event for signup where signup time has ended..")
    void getExistingEventForSignupWhereSignupHasEnded() throws Exception {
        when(signupService.getEventForSignUp(anyString(), any(Locale.class), any(ZoneId.class))).thenThrow(SignupEndedException.class);
        mockMvc.perform(get("/signup/456123")).andExpect(status().isConflict());
        verify(signupService).getEventForSignUp(eq("456123"), any(Locale.class), any(ZoneId.class));
    }

    @Test
    @DisplayName("Add participant to existing event.")
    void addParticipantToEvent() throws Exception {
        var participantAsJson = mapper.writeValueAsString(this.participant);
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var valueCapture2 = ArgumentCaptor.forClass(Participant.class);
        var valueCapture3 = ArgumentCaptor.forClass(Locale.class);
        var valueCapture4 = ArgumentCaptor.forClass(ZoneId.class);
        when(signupService.addParticipantToEvent(valueCapture.capture(), valueCapture2.capture(), valueCapture3.capture(), valueCapture4.capture()))
            .thenReturn(this.participant);
        mockMvc.perform(post("/signup/123/add").
                content(participantAsJson).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(signupService).addParticipantToEvent(anyString(), any(Participant.class), any(Locale.class), any(ZoneId.class));
        assertEquals("123", valueCapture.getValue());
        assertEquals(this.participant, valueCapture2.getValue());
    }

    @Test
    @DisplayName("Try to add participant to non-existing event.")
    void tryToAddParticipantToNonExistingEvent() throws Exception {
        var participantAsJson = mapper.writeValueAsString(this.participant);
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var valueCapture2 = ArgumentCaptor.forClass(Participant.class);
        var valueCapture3 = ArgumentCaptor.forClass(Locale.class);
        var valueCapture4 = ArgumentCaptor.forClass(ZoneId.class);
        var defaultTimeZone = ZoneId.systemDefault();
        when(signupService.addParticipantToEvent(valueCapture.capture(), valueCapture2.capture(), valueCapture3.capture(), valueCapture4.capture()))
            .thenThrow(EventNotFoundException.class);
        mockMvc.perform(post("/signup/456/add").
                content(participantAsJson).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
        verify(signupService).addParticipantToEvent(anyString(), any(Participant.class), any(Locale.class), any(ZoneId.class));
        assertEquals("456", valueCapture.getValue());
        assertEquals(this.participant, valueCapture2.getValue());
    }

    @Test
    @DisplayName("Remove a participant from event.")
    void removeParticipantFromEvent() throws Exception {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var valueCapture2 = ArgumentCaptor.forClass(Locale.class);
        var valueCapture3 = ArgumentCaptor.forClass(ZoneId.class);
        doNothing().when(signupService).removeParticipantFromEvent(valueCapture.capture(), valueCapture.capture(), valueCapture2.capture(), valueCapture3.capture());
        mockMvc.perform(delete("/signup/cancel/123/456")).andExpect(status().isOk());
        assertEquals("123", valueCapture.getAllValues().get(0));
        assertEquals("456", valueCapture.getAllValues().get(1));
    }
}
