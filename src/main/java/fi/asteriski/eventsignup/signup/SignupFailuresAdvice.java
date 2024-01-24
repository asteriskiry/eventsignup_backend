/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.exception.EventFullException;
import fi.asteriski.eventsignup.exception.SignupEndedException;
import fi.asteriski.eventsignup.exception.SignupNotStartedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SignupFailuresAdvice {

    @ResponseBody
    @ExceptionHandler(SignupNotStartedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String signupNotStarted(SignupNotStartedException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(SignupEndedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String signupEnded(SignupEndedException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(EventFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String eventFull(EventFullException ex) {
        return ex.getMessage();
    }
}
