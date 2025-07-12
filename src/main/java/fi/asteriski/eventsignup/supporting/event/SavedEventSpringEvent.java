package fi.asteriski.eventsignup.supporting.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SavedEventSpringEvent extends ApplicationEvent {
    public SavedEventSpringEvent(Object source) {
        super(source);
    }

    //    private final EventDto eventDto;
    //    private final Authentication loggedInUser;
    //    private final Locale usersLocale;
    //    private final ZoneId userTimeZone;
    //
    //    public SavedEventSpringEvent(
    //            Object source, EventDto eventDto, Authentication loggedInUser, Locale usersLocale, ZoneId
    // userTimeZone) {
    //        super(source);
    //        this.eventDto = eventDto;
    //        this.loggedInUser = loggedInUser;
    //        this.usersLocale = usersLocale;
    //        this.userTimeZone = userTimeZone;
    //    }
}
