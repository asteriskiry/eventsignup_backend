/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.config;

import java.io.IOException;
import java.util.Properties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    @NonNull
    private ApplicationContext applicationContext;

    @Value("${org.quartz.jobStore.mongoUri}")
    private String mongoUrl;

    @Value("${org.quartz.jobStore.dbName}")
    private String mongoDatabaseName;

    @Bean
    public JobFactory jobFactory() {
        var jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        var schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setQuartzProperties(quartzProperties());
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactory.setAutoStartup(true);
        schedulerFactory.setJobFactory(jobFactory());
        return schedulerFactory;
    }

    public Properties quartzProperties() throws IOException {
        var propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();

        var properties = propertiesFactoryBean.getObject();
        if (properties != null) {
            properties.setProperty("org.quartz.jobStore.mongoUri", mongoUrl);
            properties.setProperty("org.quartz.jobStore.dbName", mongoDatabaseName);
        }
        return properties;
    }
}
