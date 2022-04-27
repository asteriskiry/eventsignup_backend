package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.ZoneId;
import java.util.Locale;

@Getter
public class SavedEventSpringEvent extends ApplicationEvent {

    private final Event event;
    private final User loggedInUser;
    private final Locale usersLocale;
    private final ZoneId userTimeZone;

    public SavedEventSpringEvent(Object source, Event event, User loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        super(source);
        this.event = event;
        this.loggedInUser = loggedInUser;
        this.usersLocale = usersLocale;
        this.userTimeZone = userTimeZone;
    }
}
