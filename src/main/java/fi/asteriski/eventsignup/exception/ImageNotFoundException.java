/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.exception;

public class ImageNotFoundException extends EventSignupException {

    public ImageNotFoundException(String fileName) {
        super(String.format("Banner image with name '%s' not found.", fileName));
    }

    public ImageNotFoundException(String fileName, Exception cause) {
        super(String.format("Banner image with name '%s' not found.", fileName), cause);
    }
}
