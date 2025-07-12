/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.supporting.exception;

public class ImageDirectoryCreationFailedException extends EventSignupException {
    public ImageDirectoryCreationFailedException(String reason, Throwable cause) {
        super(reason, cause, true, false);
    }
}
