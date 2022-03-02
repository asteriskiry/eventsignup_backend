/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.event;

public class InvalidImageFileException extends RuntimeException{

    public InvalidImageFileException(String reason){
        super(reason);
    }
}
