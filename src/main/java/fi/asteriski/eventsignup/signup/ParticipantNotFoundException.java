package fi.asteriski.eventsignup.signup;

public class ParticipantNotFoundException extends RuntimeException{

    public ParticipantNotFoundException(String participantId, String eventId) {
        super(String.format("Participant %s for event %s not found.", participantId, eventId));
    }
}
