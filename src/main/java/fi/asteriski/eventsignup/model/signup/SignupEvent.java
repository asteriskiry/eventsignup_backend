/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.signup;

import fi.asteriski.eventsignup.model.event.Form;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SignupEvent(
        UUID id,
        String name,
        ZonedDateTime startDate,
        String place,
        String description,
        Form form,
        ZonedDateTime endDate,
        Double price,
        String bannerImg) {}
