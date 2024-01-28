package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.model.event.EventDto;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

@Getter
public class SavedEventSpringEvent extends ApplicationEvent {

    private final EventDto eventDto;
    private final Authentication loggedInUser;
    private final Locale usersLocale;
    private final ZoneId userTimeZone;

    public SavedEventSpringEvent(
            Object source, EventDto eventDto, Authentication loggedInUser, Locale usersLocale, ZoneId userTimeZone) {
        super(source);
        this.eventDto = eventDto;
        this.loggedInUser = loggedInUser;
        this.usersLocale = usersLocale;
        this.userTimeZone = userTimeZone;
    }
}
