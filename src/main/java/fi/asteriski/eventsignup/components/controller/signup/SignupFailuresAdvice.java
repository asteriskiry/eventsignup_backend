/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.controller.signup;

import fi.asteriski.eventsignup.supporting.exception.EventFullException;
import fi.asteriski.eventsignup.supporting.exception.SignupEndedException;
import fi.asteriski.eventsignup.supporting.exception.SignupNotStartedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
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
