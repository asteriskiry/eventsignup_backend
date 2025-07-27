package fi.asteriski.eventsignup.components.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record MyParticipants(
        List<ParticipantDto> newParticipants,
        List<ParticipantDto> upcomingParticipants,
        List<ParticipantDto> pastParticipants) {}
;
