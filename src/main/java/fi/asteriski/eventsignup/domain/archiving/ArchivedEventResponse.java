package fi.asteriski.eventsignup.domain.archiving;

import java.util.List;

public record ArchivedEventResponse(String eventOwner, List<ArchivedEventDto> events) {
}
