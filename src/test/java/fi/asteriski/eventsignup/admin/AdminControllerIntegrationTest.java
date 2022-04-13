/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.user.UserRepository;
import fi.asteriski.eventsignup.user.UserService;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static fi.asteriski.eventsignup.utils.TestUtils.generateRandomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("unchecked")
@WebMvcTest(controllers = AdminController.class)
class AdminControllerIntegrationTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private AdminService adminService;
    // These mock beans are needed since they are declared in EventsignupApplication class.
    @MockBean
    UserRepository userRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    UserService userService;
    private List<Event> events;
    private List<Participant> participants;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity()) // enable security for the mock set up
            .build();
        // This is needed so Java 8 date/time classes are supported by the mapper.
        this.mapper.registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @WithMockUser(value = "test", password = "pass", roles = {"ADMIN"})
    @DisplayName("Get all existing events for admin.")
    void getAllEvents() throws Exception {
        events = TestUtils.getRandomEvents(null);
        var eventsAsJson = mapper.writeValueAsString(events);
        when(adminService.getAllEvents()).thenReturn(events);
        performLogin();
        mockMvc.perform(get("/admin/event/all")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(eventsAsJson, true));
        verify(adminService).getAllEvents();
    }

    @Test
    @WithMockUser(value = "test", password = "pass", roles = {"ADMIN"})
    @DisplayName("Get all events owned by specific user for admin.")
    void getAllEventsForUser() throws Exception {
        var owner = "testUser";
        events = TestUtils.getRandomEvents(owner);
        var eventsAsJson = mapper.writeValueAsString(events);
        when(adminService.getAllEventsForUser(owner)).thenReturn(events);
        performLogin();
        var results = mockMvc.perform(get("/admin/event/"+owner)).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(eventsAsJson, true))
            .andReturn();

        var value = mapper.readValue(results.getResponse().getContentAsString(), List.class);
        assertTrue(value.stream().allMatch(event -> owner.equals(((Map<?, ?>) event).get("owner"))));
    }

    @Test
    @WithMockUser(value = "test", password = "pass", roles = {"ADMIN"})
    @DisplayName("Get all existing participants regardless of event for admin.")
    void getAllParticipants() throws Exception {
        participants = TestUtils.getRandomParticipants(null);
        var participantsAsJson = mapper.writeValueAsString(participants);
        when(adminService.getAllParticipants()).thenReturn(participants);
        performLogin();
        mockMvc.perform(get("/admin/participants/all")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(participantsAsJson, true));
        verify(adminService).getAllParticipants();
    }

    @Test
    @WithMockUser(value = "test", password = "pass", roles = {"ADMIN"})
    @DisplayName("Get all participants for a single event for admin.")
    void getAllParticipantsForEvent() throws Exception {
        participants = TestUtils.getRandomParticipants("123");
        var participantsAsJson = mapper.writeValueAsString(participants);
        when(adminService.getAllParticipantsForEvent("123")).thenReturn(participants);
        performLogin();
        var results = mockMvc.perform(get("/admin/participants/123")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(participantsAsJson, true))
                .andReturn();
        verify(adminService).getAllParticipantsForEvent("123");
        var value = mapper.readValue(results.getResponse().getContentAsString(), List.class);
        assertTrue(value.stream().allMatch(participant -> "123".equals(((Map<?, ?>) participant).get("event"))));
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get all events thru admin interface as normal user.")
    void tryToGetAllEventsFromAdminAsNormalUser() throws Exception {
        performLogin();
        mockMvc.perform(get("/admin/event/all")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get all participants thru admin interface as normal user.")
    void tryToGetAllParticipantsFromAdminAsNormalUser() throws Exception {
        performLogin();
        mockMvc.perform(get("/admin/participants/all")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get all events thru admin interface as normal user.")
    void tryToGetAllEventsForUserFromAdminAsNormalUser() throws Exception {
        performLogin();
        mockMvc.perform(get("/admin/event/123")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get all events thru admin interface as normal user.")
    void tryToGetAllParticipantsForUserFromAdminAsNormalUser() throws Exception {
        performLogin();
        mockMvc.perform(get("/admin/participants/123")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Try to get all events thru admin interface when not logged in.")
    void tryToGetAllParticipantsForUserFromAdminWhenNotLoggedIn() throws Exception {
        var result = mockMvc.perform(get("/admin/participants/123")).andReturn();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Try to get all events thru admin interface when not logged in.")
    void tryToGetAllEventsForUserFromAdminWhenNotLoggedIn() throws Exception {
        var result = mockMvc.perform(get("/admin/event/123")).andReturn();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Try to get all events thru admin interface when not logged in.")
    void tryToGetAllEventsFromAdminWhenNotLoggedIn() throws Exception {
        var result = mockMvc.perform(get("/admin/event/all")).andReturn();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Try to get all events thru admin interface when not logged in.")
    void tryToGetAllParticipantsFromAdminWhenNotLoggedIn() throws Exception {
        var result = mockMvc.perform(get("/admin/participants/all")).andReturn();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @RepeatedTest(100)
    @DisplayName("Try to get random urls under /admin when not logged in.")
    void testRandomAdminUrlsWhenNotLoggedIn() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        var result = mockMvc.perform(get(String.format("/admin/%s", randomString))).andReturn();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    @RepeatedTest(100)
    @WithMockUser(value = "test", password = "pass")
    @DisplayName("Try to get random urls under /admin when logged in as normal user.")
    void testRandomAdminUrlsWhenLoggedInAsNormalUser() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        performLogin();
        mockMvc.perform(get(String.format("/admin/%s", randomString))).andExpect(status().isForbidden());
    }

    @RepeatedTest(100)
    @WithMockUser(value = "test", password = "pass", roles = {"ADMIN"})
    @DisplayName("Try to get random urls under /admin when logged in as admin.")
    void testRandomAdminUrlsWhenLoggedInAsAdmin() throws Exception {
        var rnd = new Random();
        var randomString = generateRandomString(rnd.nextInt(5, 31));
        performLogin();
        mockMvc.perform(get(String.format("/admin/%s", randomString))).andExpect(status().isNotFound());
    }

    private void performLogin() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().is3xxRedirection());
    }
}
