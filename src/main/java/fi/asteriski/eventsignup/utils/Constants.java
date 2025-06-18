/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.exception.EventNotFoundException;
import fi.asteriski.eventsignup.exception.FormNotFoundException;
import java.time.ZoneId;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;

@UtilityClass
public final class Constants {

    public static final Limit MAX_FETCHED_EVENTS = Limit.of(5);
    public static final Sort SORT_BY_CREATED_AT_DESC = Sort.by(Sort.Direction.DESC, "createdAt");
    public static final Supplier<EventNotFoundException> EVENT_NOT_FOUND_EXCEPTION_SUPPLIER =
            () -> new EventNotFoundException("Event not found.");
    public static final Supplier<FormNotFoundException> FORM_NOT_FOUND_EXCEPTION_SUPPLIER =
            () -> new FormNotFoundException("Form not found");

    public static final String API_PATH_ARCHIVE = "/api/v1/archive";
    public static final String API_PATH_ADMIN = "/api/v1/admin";
    public static final String API_PATH_EVENT = "/api/v1/event";
    public static final String API_PATH_SIGNUP = "/api/v1/signup";
    public static final String API_PATH_FORM = "/api/v1/form";
    public static final ZoneId UTC_TIME_ZONE = ZoneId.of("Z");
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
}
