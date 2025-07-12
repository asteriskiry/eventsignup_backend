/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.supporting.exception;

public class FileMoveNotSuccessfulException extends RuntimeException {
    public FileMoveNotSuccessfulException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
