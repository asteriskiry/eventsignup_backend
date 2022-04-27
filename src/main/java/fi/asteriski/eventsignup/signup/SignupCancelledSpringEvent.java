package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SignupCancelledSpringEvent extends ApplicationEvent {

    @Getter
    private final Event event;
    @Getter
    private final Participant participant;

    public SignupCancelledSpringEvent(Object source, Event event, Participant participant) {
        super(source);
        this.event = event;
        this.participant = participant;
    }
}
