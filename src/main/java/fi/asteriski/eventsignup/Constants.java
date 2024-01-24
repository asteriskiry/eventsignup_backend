/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2023.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup;

import java.time.ZoneId;

public final class Constants {
    private Constants(){}
    public static final String API_PATH_ARCHIVE = "/api/archive";
    public static final String API_PATH_ADMIN = "/api/admin";
    public static final String API_PATH_EVENT = "/api/event";
    public static final String API_PATH_SIGNUP = "/api/signup";
    public static final ZoneId UTC_TIME_ZONE = ZoneId.of("Z");

}
