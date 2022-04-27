package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.ZoneId;
import java.util.Locale;

public class SignupSuccessfulSpringEvent extends ApplicationEvent {

    @Getter
    private final Event event;
    @Getter
    private final Participant participant;
    @Getter
    private final Locale userLocale;
    @Getter
    private final ZoneId userTimeZone;

    public SignupSuccessfulSpringEvent(Object source, Event event, Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        super(source);
        this.event = event;
        this.participant = participant;
        this.userLocale = usersLocale;
        this.userTimeZone = userTimeZone;
    }
}
