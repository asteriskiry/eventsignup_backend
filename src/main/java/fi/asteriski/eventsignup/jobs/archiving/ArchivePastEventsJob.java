/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.jobs.archiving;

import fi.asteriski.eventsignup.service.archiving.ArchivedEventService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class ArchivePastEventsJob {

    private ArchivedEventService archivedEventService;

    @Scheduled(cron = "@weekly")
    public void archivePastEventsJob() {
        log.info(String.format("[%s] Running ArchivePastEventsJob Job.", LocalDateTime.now()));
        archivedEventService.archivePastEvents();
    }
}
