/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.jobs;

import fi.asteriski.eventsignup.event.ArchivedEventService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class RemoveOldArchiveEventsJob {
    private ArchivedEventService archivedEventService;

    @Scheduled(cron = "@yearly")
    public void removeOldArchivedEvents() {
        log.info(String.format("[%s] Running removeArchivedEventsOlderThanOneYear Job.", LocalDateTime.now()));
        archivedEventService.removeArchivedEventsOlderThanOneYear();
    }
}
