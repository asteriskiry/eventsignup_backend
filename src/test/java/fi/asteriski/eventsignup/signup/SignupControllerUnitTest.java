/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fi.asteriski.eventsignup.controller.signup.SignupController;
import fi.asteriski.eventsignup.exception.EventFullException;
import fi.asteriski.eventsignup.exception.EventNotFoundException;
import fi.asteriski.eventsignup.exception.SignupEndedException;
import fi.asteriski.eventsignup.exception.SignupNotStartedException;
import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.model.signup.SignupEvent;
import fi.asteriski.eventsignup.service.signup.SignupServiceImpl;
import fi.asteriski.eventsignup.utils.TestUtils;
import java.time.ZoneId;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class SignupControllerUnitTest {

    private SignupServiceImpl signupService;
    private SignupController signupController;
    private ParticipantDto participant;
    private EventDto event;

    @BeforeEach
    void setUp() {
        signupService = Mockito.mock(SignupServiceImpl.class);
        signupController = new SignupController(signupService);
        participant = TestUtils.createRandomParticipant(null);
        event = TestUtils.createRandomEvent(null);
    }

    @Test
    void getEventForSignup_givenEventExists_expectSignupEventInstance() {
        when(signupService.getEventForSignUp(eq("123"), any(Locale.class), any(ZoneId.class)))
                .thenReturn(event.toSignupEvent());

        assertInstanceOf(
                SignupEvent.class,
                signupController.getEventForSignup("123", Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_givenEventDoesNotExist_throwsEventNotFoundException() {
        when(signupService.getEventForSignUp(eq("123"), any(Locale.class), any(ZoneId.class)))
                .thenThrow(EventNotFoundException.class);

        assertThrows(
                EventNotFoundException.class,
                () -> signupController.getEventForSignup("123", Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_givenEventExistAndItIsAlreadyFull_throwsEventFullException() {
        when(signupService.getEventForSignUp(eq("123"), any(Locale.class), any(ZoneId.class)))
                .thenThrow(EventFullException.class);

        assertThrows(
                EventFullException.class,
                () -> signupController.getEventForSignup("123", Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_eventExistsButSignupHasNotStarted_throwsSignupNotStartedException() {
        when(signupService.getEventForSignUp(eq("123"), any(Locale.class), any(ZoneId.class)))
                .thenThrow(SignupNotStartedException.class);

        assertThrows(
                SignupNotStartedException.class,
                () -> signupController.getEventForSignup("123", Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_eventExistsButSignupHasEnded_throwsSignupEndedException() {
        when(signupService.getEventForSignUp(eq("123"), any(Locale.class), any(ZoneId.class)))
                .thenThrow(SignupEndedException.class);

        assertThrows(
                SignupEndedException.class,
                () -> signupController.getEventForSignup("123", Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void addParticipantToEvent_eventExistsAndIsNotFull_participantIsAddedToEvent() {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var valueCapture2 = ArgumentCaptor.forClass(ParticipantDto.class);
        var valueCapture3 = ArgumentCaptor.forClass(Locale.class);
        var valueCapture4 = ArgumentCaptor.forClass(ZoneId.class);
        var defaultLocale = Locale.getDefault();
        var defaultTimeZone = ZoneId.systemDefault();
        when(signupService.addParticipantToEvent(
                        valueCapture.capture(),
                        valueCapture2.capture(),
                        valueCapture3.capture(),
                        valueCapture4.capture()))
                .thenReturn(this.participant);

        signupController.addParticipantToEvent("123", participant, defaultLocale, defaultTimeZone);

        verify(signupService).addParticipantToEvent("123", participant, defaultLocale, defaultTimeZone);
        assertEquals("123", valueCapture.getValue());
        assertEquals(participant, valueCapture2.getValue());
    }

    @Test
    void removeParticipantFromEvent_eventExistsAndParticipantHasSignedUpToIt_participantIsRemovedFromEvent() {
        var valueCapture = ArgumentCaptor.forClass(String.class);
        var valueCapture3 = ArgumentCaptor.forClass(Locale.class);
        var valueCapture4 = ArgumentCaptor.forClass(ZoneId.class);
        doNothing()
                .when(signupService)
                .removeParticipantFromEvent(
                        valueCapture.capture(),
                        valueCapture.capture(),
                        valueCapture3.capture(),
                        valueCapture4.capture());

        signupController.removeParticipantFromEvent("123", "456", Locale.getDefault(), ZoneId.systemDefault());

        verify(signupService).removeParticipantFromEvent(eq("123"), eq("456"), any(Locale.class), any(ZoneId.class));
        assertEquals("123", valueCapture.getAllValues().get(0));
        assertEquals("456", valueCapture.getAllValues().get(1));
    }
}
