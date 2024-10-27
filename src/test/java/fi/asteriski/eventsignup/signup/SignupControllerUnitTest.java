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
import java.util.UUID;
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
        var id = UUID.randomUUID();
        when(signupService.getEventForSignUp(eq(id), any(Locale.class), any(ZoneId.class)))
                .thenReturn(event.toSignupEvent());

        assertInstanceOf(
                SignupEvent.class, signupController.getEventForSignup(id, Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_givenEventDoesNotExist_throwsEventNotFoundException() {
        var id = UUID.randomUUID();
        when(signupService.getEventForSignUp(eq(id), any(Locale.class), any(ZoneId.class)))
                .thenThrow(EventNotFoundException.class);

        assertThrows(
                EventNotFoundException.class,
                () -> signupController.getEventForSignup(id, Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_givenEventExistAndItIsAlreadyFull_throwsEventFullException() {
        var id = UUID.randomUUID();
        when(signupService.getEventForSignUp(eq(id), any(Locale.class), any(ZoneId.class)))
                .thenThrow(EventFullException.class);

        assertThrows(
                EventFullException.class,
                () -> signupController.getEventForSignup(id, Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_eventExistsButSignupHasNotStarted_throwsSignupNotStartedException() {
        var id = UUID.randomUUID();
        when(signupService.getEventForSignUp(eq(id), any(Locale.class), any(ZoneId.class)))
                .thenThrow(SignupNotStartedException.class);

        assertThrows(
                SignupNotStartedException.class,
                () -> signupController.getEventForSignup(id, Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void getEventForSignup_eventExistsButSignupHasEnded_throwsSignupEndedException() {
        var id = UUID.randomUUID();
        when(signupService.getEventForSignUp(eq(id), any(Locale.class), any(ZoneId.class)))
                .thenThrow(SignupEndedException.class);

        assertThrows(
                SignupEndedException.class,
                () -> signupController.getEventForSignup(id, Locale.getDefault(), ZoneId.systemDefault()));
    }

    @Test
    void addParticipantToEvent_eventExistsAndIsNotFull_participantIsAddedToEvent() {
        var id = UUID.randomUUID();
        var valueCapture = ArgumentCaptor.forClass(UUID.class);
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

        signupController.addParticipantToEvent(id, participant, defaultLocale, defaultTimeZone);

        verify(signupService).addParticipantToEvent(id, participant, defaultLocale, defaultTimeZone);
        assertEquals(id, valueCapture.getValue());
        assertEquals(participant, valueCapture2.getValue());
    }

    @Test
    void removeParticipantFromEvent_eventExistsAndParticipantHasSignedUpToIt_participantIsRemovedFromEvent() {
        var id = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var valueCapture = ArgumentCaptor.forClass(UUID.class);
        var valueCapture3 = ArgumentCaptor.forClass(Locale.class);
        var valueCapture4 = ArgumentCaptor.forClass(ZoneId.class);
        doNothing()
                .when(signupService)
                .removeParticipantFromEvent(
                        valueCapture.capture(),
                        valueCapture.capture(),
                        valueCapture3.capture(),
                        valueCapture4.capture());

        signupController.removeParticipantFromEvent(id, id2, Locale.getDefault(), ZoneId.systemDefault());

        verify(signupService).removeParticipantFromEvent(eq(id), eq(id2), any(Locale.class), any(ZoneId.class));
        assertEquals(id, valueCapture.getAllValues().get(0));
        assertEquals(id2, valueCapture.getAllValues().get(1));
    }
}
