/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.supporting.exception;

public class EventFullException extends EventSignupException {

    public EventFullException(String reason) {
        super(reason, null, true, false);
    }
}
