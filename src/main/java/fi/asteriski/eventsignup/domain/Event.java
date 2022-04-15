/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@Document
@Data
public class Event {

    @Id
    private String id;
    @NonNull // For lombok
    @NotNull // For openApi
    private String name;
    @NonNull // For lombok
    @NotNull // For openApi
    private Instant startDate;
    @NonNull // For lombok
    @NotNull // For openApi
    private String place;
    @NonNull // For lombok
    @NotNull // For openApi
    private String description;
    @NonNull // For lombok
    @NotNull // For openApi
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
