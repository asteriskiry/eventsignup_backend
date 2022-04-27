package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.event.SavedEventSpringEvent;
import fi.asteriski.eventsignup.signup.SignupCancelledSpringEvent;
import fi.asteriski.eventsignup.signup.SignupSuccessfulSpringEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishSavedEventEvent(final Event event, final User loggedInUser) {
        SavedEventSpringEvent savedEventSpringEvent = new SavedEventSpringEvent(this, event, loggedInUser);
        applicationEventPublisher.publishEvent(savedEventSpringEvent);
    }

    public void publishSignupSuccessfulEvent(final Event event, final Participant participant) {
        SignupSuccessfulSpringEvent signupSuccessfulSpringEvent = new SignupSuccessfulSpringEvent(this, event, participant);
        applicationEventPublisher.publishEvent(signupSuccessfulSpringEvent);
    }

    public void publishSignupCancelledEvent(final Event event, final Participant participant) {
        SignupCancelledSpringEvent signupCancelledSpringEvent = new SignupCancelledSpringEvent(this, event, participant);
        applicationEventPublisher.publishEvent(signupCancelledSpringEvent);
    }
}
