package fi.asteriski.eventsignup.model.archiving;

import java.util.List;

public record ArchivedEventResponse(String eventOwner, List<ArchivedEventDto> events) {
}
