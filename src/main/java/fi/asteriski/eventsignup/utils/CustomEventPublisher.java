/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.signup.Participant;
import fi.asteriski.eventsignup.event.SavedEventSpringEvent;
import fi.asteriski.eventsignup.signup.SignupCancelledSpringEvent;
import fi.asteriski.eventsignup.signup.SignupSuccessfulSpringEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Locale;

@Component
@AllArgsConstructor
public class CustomEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishSavedEventEvent(final EventDto eventDto, final Authentication loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        SavedEventSpringEvent savedEventSpringEvent = new SavedEventSpringEvent(this, eventDto, loggedInUser, usersLocale, userTimeZone);
        applicationEventPublisher.publishEvent(savedEventSpringEvent);
    }

    public void publishSignupSuccessfulEvent(final EventDto eventDto, final Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        SignupSuccessfulSpringEvent signupSuccessfulSpringEvent = new SignupSuccessfulSpringEvent(this, eventDto, participant, usersLocale, userTimeZone);
        applicationEventPublisher.publishEvent(signupSuccessfulSpringEvent);
    }

    public void publishSignupCancelledEvent(final EventDto eventDto, final Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        SignupCancelledSpringEvent signupCancelledSpringEvent = new SignupCancelledSpringEvent(this, eventDto, participant, usersLocale, userTimeZone);
        applicationEventPublisher.publishEvent(signupCancelledSpringEvent);
    }
}
