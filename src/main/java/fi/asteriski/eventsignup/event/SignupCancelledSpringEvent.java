/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SignupCancelledSpringEvent extends ApplicationEvent {

    private final EventDto eventDto;
    private final ParticipantDto participantDto;
    private final Locale usersLocale;
    private final ZoneId userTimeZone;

    public SignupCancelledSpringEvent(
            Object source, EventDto eventDto, ParticipantDto participantDto, Locale usersLocale, ZoneId userTimeZone) {
        super(source);
        this.eventDto = eventDto;
        this.participantDto = participantDto;
        this.usersLocale = usersLocale;
        this.userTimeZone = userTimeZone;
    }
}
