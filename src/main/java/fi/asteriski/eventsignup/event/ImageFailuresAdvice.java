/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

import fi.asteriski.eventsignup.exception.ImageDirectoryCreationFailedException;
import fi.asteriski.eventsignup.exception.ImageNotFoundException;
import fi.asteriski.eventsignup.exception.InvalidImageFileException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ImageFailuresAdvice {

    @ResponseBody
    @ExceptionHandler(ImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String bannerImageNotFoundHandler(ImageNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidImageFileException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String invalidImageFileHandler(InvalidImageFileException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ImageDirectoryCreationFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String imageDirectoryCreationFailed(ImageDirectoryCreationFailedException ex) {
        return ex.getMessage();
    }
}
