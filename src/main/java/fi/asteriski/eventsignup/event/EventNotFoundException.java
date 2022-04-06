/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String id) {
        super(String.format("Event with id '%s' not found.", id));
    }
}