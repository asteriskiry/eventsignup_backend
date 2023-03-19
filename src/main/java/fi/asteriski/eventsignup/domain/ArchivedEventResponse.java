package fi.asteriski.eventsignup.domain;

import java.util.List;

public record ArchivedEventResponse(String eventOwner, List<ArchivedEventDto> events) {
}
