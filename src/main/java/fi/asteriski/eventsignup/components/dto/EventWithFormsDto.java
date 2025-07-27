package fi.asteriski.eventsignup.components.dto;

import java.util.List;

public record EventWithFormsDto(
    EventDto event,
    List<FormDto> forms
) {}
