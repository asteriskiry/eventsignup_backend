/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.event.EventNotFoundException;
import fi.asteriski.eventsignup.event.EventService;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@AllArgsConstructor
@Service
public class SignupService {

    EventService eventService;
    ParticipantRepository participantRepository;
    private CustomEventPublisher customEventPublisher;


    public Event getEventForSignUp(String eventId) {
        Event event = eventService.getEvent(eventId);
        Instant now = Instant.now();
        if (event.getSignupStarts() != null && event.getSignupStarts().isAfter(now)) {
            ZonedDateTime zonedDateTime = ZonedDateTime.from(event.getSignupStarts().atZone(ZoneId.systemDefault()));
            String formattedZonedDateTime = zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String errorMsg = String.format("Signup for event '%s' has not started yet. It starts %s.", event.getName(), formattedZonedDateTime);
            throw new SignupNotStartedException(errorMsg);
        }
        if (event.getSignupEnds() != null && event.getSignupEnds().isBefore(now)) {
            throw new SignupEndedException(String.format("Signup for event '%s' has ended.", event.getName()));
        }
        if (event.getStartDate().isBefore(now)) {
            throw new EventNotFoundException(String.format("Event '%s' not found.", event.getName()));
        }
        if (event.getMaxParticipants() != null) {
            long numOfParticipants = participantRepository.countAllByEvent(eventId);
            if (numOfParticipants >= event.getMaxParticipants()) {
                throw new EventFullException(String.format("Signup failed. Event '%s' is already full.", event.getName()));
            }
        }
        return event;
    }

    public Participant addParticipantToEvent(String eventId, Participant participant) {
        if (!eventId.equals(participant.getEvent())) {
            throw new EventNotFoundException(participant.getEvent());
        }
        if (!eventService.eventExists(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        participant.setSignupTime(Instant.now());
        customEventPublisher.publishSignupSuccessfulEvent(eventService.getEvent(eventId), participant);
        return participantRepository.save(participant);
    }

    public void removeParticipantFromEvent(String eventId, String participantId) {
        if (!eventService.eventExists(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        Participant participant = participantRepository.findById(participantId).orElseThrow(() -> new ParticipantNotFoundException(participantId, eventId));
        participantRepository.deleteParticipantByEventAndId(eventId, participantId);
        customEventPublisher.publishSignupCancelledEvent(eventService.getEvent(eventId), participant);
    }
}
