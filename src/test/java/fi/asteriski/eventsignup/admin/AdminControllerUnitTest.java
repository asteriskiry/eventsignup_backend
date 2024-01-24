/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.admin;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.signup.Participant;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminControllerUnitTest {

    private AdminController adminController;
    private AdminService adminService;
    private List<Participant> participants;
    private List<Event> events;

    @BeforeEach
    void setUp() {
        adminService = Mockito.mock(AdminService.class);
        adminController = new AdminController(adminService);
        participants = TestUtils.getRandomParticipants(null);
        events = TestUtils.getRandomEvents(null);
    }

    @Test
    @DisplayName("Get all events.")
    void getAllEvents() {
        when(adminService.getAllEvents()).thenReturn(events);
        assertInstanceOf(List.class, adminController.getAllEvents());
    }

    @Test
    @DisplayName("Get all events for specific user.")
    void getAllEventsForUser() {
        var user = "testUser";
        var valueCapture = ArgumentCaptor.forClass(String.class);
        when(adminService.getAllEventsForUser(valueCapture.capture())).thenReturn(events);
        adminController.getAllEventsForUser(user);
        verify(adminService).getAllEventsForUser(user);
        assertEquals(user, valueCapture.getValue());
    }

    @Test
    @DisplayName("Get all participants regardless of event.")
    void getAllParticipants() {
        when(adminService.getAllParticipants()).thenReturn(participants);
        assertInstanceOf(List.class, adminController.getAllParticipants());
    }

    @Test
    @DisplayName("Get participants for a specific event.")
    void getAllParticipantsForEvent() {
        var eventId = "123";
        var valueCapture = ArgumentCaptor.forClass(String.class);
        when(adminService.getAllParticipantsForEvent(valueCapture.capture())).thenReturn(participants);
        adminController.getAllParticipantsForEvent(eventId);
        verify(adminService).getAllParticipantsForEvent(eventId);
        assertEquals(eventId, valueCapture.getValue());
    }
}
