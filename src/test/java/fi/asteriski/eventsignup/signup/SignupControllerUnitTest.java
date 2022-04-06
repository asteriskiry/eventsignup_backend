/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.event.EventNotFoundException;
import fi.asteriski.eventsignup.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SignupControllerUnitTest {

    private SignupService signupService;
    private SignupController signupController;
    private Participant participant;
    private Event event;

    @BeforeEach
    void setUp() {
        signupService = Mockito.mock(SignupService.class);
        signupController = new SignupController(signupService);
        participant = TestUtils.createRandomParticipant();
        event = TestUtils.createRandomEvent();
    }

    @Test
    @DisplayName("Get an existing event for signup.")
    void getExistingEventForSignup() {
        when(signupService.getEventForSignUp("123")).thenReturn(this.event);
        assertInstanceOf(Event.class, signupController.getEventForSignup("123"));
    }

    @Test
    @DisplayName("Get an non-existing event for signup.")
    void getNonExistingEventForSignup() {
        when(signupService.getEventForSignUp("123")).thenThrow(EventNotFoundException.class);
        assertThrows(EventNotFoundException.class, () -> signupController.getEventForSignup("123"));
    }

    @Test
    @DisplayName("Get an existing event for signup which is already full.")
    void getEventForSignupThatIsFull() {
        when(signupService.getEventForSignUp("123")).thenThrow(EventFullException.class);
        assertThrows(EventFullException.class, () -> signupController.getEventForSignup("123"));
    }

    @Test
    @DisplayName("Get an existing event for signup where signup hasn't started yet.")
    void getEventForSignupThatHasNotStartedYet() {
        when(signupService.getEventForSignUp("123")).thenThrow(SignupNotStartedException.class);
        assertThrows(SignupNotStartedException.class, () -> signupController.getEventForSignup("123"));
    }

    @Test
    @DisplayName("Get an existing event for signup where signup has ended.")
    void getEventForSignupThatSignupHasEnded() {
        when(signupService.getEventForSignUp("123")).thenThrow(SignupEndedException.class);
        assertThrows(SignupEndedException.class, () -> signupController.getEventForSignup("123"));
    }

    @Test
    @DisplayName("Add a participant to event.")
    void addParticipantToEvent() {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var valueCapture2 = ArgumentCaptor.forClass(Participant.class);
        when(signupService.addParticipantToEvent(valueCapture.capture(), valueCapture2.capture())).thenReturn(this.participant);
        signupController.addParticipantToEvent("123", participant);
        verify(signupService).addParticipantToEvent("123", this.participant);
        assertEquals("123", valueCapture.getValue());
        assertEquals(this.participant, valueCapture2.getValue());
    }

    @Test
    @DisplayName("Remove a participant from event.")
    void removeParticipantFromEvent() {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var valueCapture2 = ArgumentCaptor.forClass(String.class);
        doNothing().when(signupService).removeParticipantFromEvent(valueCapture.capture(), valueCapture2.capture());
        signupController.removeParticipantFromEvent("123", "456");
        verify(signupService).removeParticipantFromEvent("123", "456");
        assertEquals("123", valueCapture.getValue());
        assertEquals("456", valueCapture2.getValue());
    }
}
