/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.exception;

/*
This class exist only to make test writing easier.
All subclasses originally extended RuntimeException.
However, checking whether a method throws a RuntimeException in tests is not particularly useful since many other components can throw that too.
Thus having a custom super class for expected exceptions makes it easier to check that the exception originated in the component being tested.
*/

public class EventSignupException extends RuntimeException {
    public EventSignupException(String reason, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(reason, cause, enableSuppression, writableStackTrace);
    }

    public EventSignupException(String reason) {
        super(reason);
    }

    public EventSignupException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
