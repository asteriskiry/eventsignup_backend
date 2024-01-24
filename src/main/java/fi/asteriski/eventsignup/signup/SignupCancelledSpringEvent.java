/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.signup.Participant;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.ZoneId;
import java.util.Locale;

@Getter
public class SignupCancelledSpringEvent extends ApplicationEvent {

    private final EventDto eventDto;
    private final Participant participant;
    private final Locale usersLocale;
    private final ZoneId userTimeZone;


    public SignupCancelledSpringEvent(Object source, EventDto eventDto, Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        super(source);
        this.eventDto = eventDto;
        this.participant = participant;
        this.usersLocale = usersLocale;
        this.userTimeZone = userTimeZone;
    }
}
