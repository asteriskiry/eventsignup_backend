/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.model.archiving;

import java.time.ZonedDateTime;

public record RemoveArchivedEventsRequest(ZonedDateTime dateLimit) {}
