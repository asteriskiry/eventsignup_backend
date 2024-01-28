/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.signup.ParticipantDto;
import fi.asteriski.eventsignup.event.SavedEventSpringEvent;
import fi.asteriski.eventsignup.event.SignupCancelledSpringEvent;
import fi.asteriski.eventsignup.event.SignupSuccessfulSpringEvent;
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
        var savedEventSpringEvent = new SavedEventSpringEvent(this, eventDto, loggedInUser, usersLocale, userTimeZone);
        applicationEventPublisher.publishEvent(savedEventSpringEvent);
    }

    public void publishSignupSuccessfulEvent(final EventDto eventDto, final ParticipantDto participantEntity, Locale usersLocale, ZoneId userTimeZone) {
        var signupSuccessfulSpringEvent = new SignupSuccessfulSpringEvent(this, eventDto, participantEntity, usersLocale, userTimeZone);
        applicationEventPublisher.publishEvent(signupSuccessfulSpringEvent);
    }

    public void publishSignupCancelledEvent(final EventDto eventDto, final ParticipantDto participantEntity, Locale usersLocale, ZoneId userTimeZone) {
        var signupCancelledSpringEvent = new SignupCancelledSpringEvent(this, eventDto, participantEntity, usersLocale, userTimeZone);
        applicationEventPublisher.publishEvent(signupCancelledSpringEvent);
    }
}
