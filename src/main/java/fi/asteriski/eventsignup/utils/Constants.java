/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import java.time.ZoneId;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {

    public static final String API_PATH_ARCHIVE = "/api/v1/archive";
    public static final String API_PATH_ADMIN = "/api/v1/admin";
    public static final String API_PATH_EVENT = "/api/v1/event";
    public static final String API_PATH_SIGNUP = "/api/v1/signup";
    public static final ZoneId UTC_TIME_ZONE = ZoneId.of("Z");
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
}
