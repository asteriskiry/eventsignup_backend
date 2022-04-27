package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.ZoneId;
import java.util.Locale;

@Getter
public class SignupCancelledSpringEvent extends ApplicationEvent {

    private final Event event;
    private final Participant participant;
    private final Locale usersLocale;
    private final ZoneId userTimeZone;


    public SignupCancelledSpringEvent(Object source, Event event, Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        super(source);
        this.event = event;
        this.participant = participant;
        this.usersLocale = usersLocale;
        this.userTimeZone = userTimeZone;
    }
}
