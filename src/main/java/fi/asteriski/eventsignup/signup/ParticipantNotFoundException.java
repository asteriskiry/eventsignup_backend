package fi.asteriski.eventsignup.signup;

public class ParticipantNotFoundException extends RuntimeException{

    public ParticipantNotFoundException(String message) {
        super(message);
    }
}
