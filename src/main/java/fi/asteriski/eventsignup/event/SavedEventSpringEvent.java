package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SavedEventSpringEvent extends ApplicationEvent {

    @Getter
    private final Event event;
    @Getter
    private final User loggedInUser;

    public SavedEventSpringEvent(Object source, Event event, User loggedInUser) {
        super(source);
        this.event = event;
        this.loggedInUser = loggedInUser;
    }
}
