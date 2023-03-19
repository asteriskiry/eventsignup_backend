/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record ArchivedEventDto(String id, EventDto originalEvent, ZonedDateTime dateArchived, Long numberOfParticipants,
                               String originalOwner) {
}
