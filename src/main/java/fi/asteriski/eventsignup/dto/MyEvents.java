/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dto;

import java.util.List;

public record MyEvents(
        List<EventDto> uusimmatTapahtumat, List<EventDto> tulevatTapahtumat, List<EventDto> menneetTapahtumat) {}
