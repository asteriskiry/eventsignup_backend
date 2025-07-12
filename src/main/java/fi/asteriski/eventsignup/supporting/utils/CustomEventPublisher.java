/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.supporting.utils;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    //    public void publishSavedEventEvent(
    //            final EventDto eventDto, final Authentication loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
    //        var savedEventSpringEvent = new SavedEventSpringEvent(this, eventDto, loggedInUser, usersLocale,
    // userTimeZone);
    //        applicationEventPublisher.publishEvent(savedEventSpringEvent);
    //    }
    //
    //    public void publishSignupSuccessfulEvent(
    //            final EventDto eventDto, final ParticipantDto participantEntity, Locale usersLocale, ZoneId
    // userTimeZone) {
    //        var signupSuccessfulSpringEvent =
    //                new SignupSuccessfulSpringEvent(this, eventDto, participantEntity, usersLocale, userTimeZone);
    //        applicationEventPublisher.publishEvent(signupSuccessfulSpringEvent);
    //    }
    //
    //    public void publishSignupCancelledEvent(
    //            final EventDto eventDto, final ParticipantDto participantEntity, Locale usersLocale, ZoneId
    // userTimeZone) {
    //        var signupCancelledSpringEvent =
    //                new SignupCancelledSpringEvent(this, eventDto, participantEntity, usersLocale, userTimeZone);
    //        applicationEventPublisher.publishEvent(signupCancelledSpringEvent);
    //    }
}
