/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.archiving;

import fi.asteriski.eventsignup.model.archiving.ArchivedEventDto;
import fi.asteriski.eventsignup.model.archiving.ArchivedEventResponse;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

public interface ArchivedEventService {
    ArchivedEventDto archiveEvent(String eventId, Locale usersLocale);

    void archivePastEvents();

    List<ArchivedEventResponse> getAllArchivedEvents();

    List<ArchivedEventDto> getAllArchivedEventsForUser(String userId);

    void removeArchivedEventsBeforeDate(Instant dateLimit);

    void removeArchivedEvent(String archivedEventId);

    void removeArchivedEventsOlderThanOneYear();
}
