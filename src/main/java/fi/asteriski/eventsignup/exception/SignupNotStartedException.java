/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.exception;

public class SignupNotStartedException extends EventSignupException {

    public SignupNotStartedException(String reason) {
        super(reason, null, true, false);
    }
}
