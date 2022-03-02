/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.Map;

@Data
public class Event {

    @Id
    private String id;
    @NonNull
    private String name;
    @NonNull
    private Instant startDate;
    @NonNull
    private String place;
    @NonNull
    private String description;
    @NonNull
    private Form form;
    @Indexed
    private String owner;
    private Instant endDate;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Instant signupStarts;
    private Instant signupEnds;
    private Map<String, Object> quotas;
    private String bannerImg;
    private Map<String, Object> otherData;
    private Map<String, Object> metaData;

}
