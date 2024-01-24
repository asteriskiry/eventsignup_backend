/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import fi.asteriski.eventsignup.domain.event.Form;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record SignupEvent(String id,
                          String name,
                          ZonedDateTime startDate,
                          String place,
                          String description,
                          Form form,
                          ZonedDateTime endDate,
                          Double price,
                          String bannerImg
) {
}
