/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import fi.asteriski.eventsignup.jobs.RemoveOldArchiveEventsJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class ScheduledJobsConfig {
    @Bean
    public RemoveOldArchiveEventsJob removeOldArchivedEvents() {return new RemoveOldArchiveEventsJob();}
}
