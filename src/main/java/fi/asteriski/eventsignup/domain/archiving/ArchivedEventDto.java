/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain.archiving;

import fi.asteriski.eventsignup.domain.event.EventDto;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record ArchivedEventDto(String id,
                               EventDto originalEvent,
                               ZonedDateTime dateArchived,
                               Long numberOfParticipants,
                               String originalOwner,
                               String bannerImage
) {
    public ArchivedEventEntity toEntity() {
        return ArchivedEventEntity.builder()
            .id(id)
            .originalEvent(originalEvent.toEntity())
            .dateArchived(dateArchived.toInstant())
            .numberOfParticipants(numberOfParticipants)
            .originalOwner(originalOwner)
            .bannerImage(bannerImage)
            .build();
    }
}
