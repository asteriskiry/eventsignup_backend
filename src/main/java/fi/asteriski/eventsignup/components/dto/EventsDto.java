/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dto;

import lombok.Builder;

@Builder
public record EventsDto(MyEvents myEvents, MyForms myForms) {}
