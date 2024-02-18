/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@Data
public class Form {

    /*
    NOTE!
    Fields must not have @NonNull annotation.
    Otherwise, conversion from incoming json to Event in EventController will fail!
     */
    private Instant dateCreated;
    @NotNull // for openAPI
    private Map<String, Object> formData;
    private String userCreated;
}
