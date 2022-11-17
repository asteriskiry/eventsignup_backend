/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.SignupEvent;
import fi.asteriski.eventsignup.event.EventNotFoundException;
import fi.asteriski.eventsignup.event.EventService;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Log4j2
@AllArgsConstructor
@Service
public class SignupService {

    EventService eventService;
    ParticipantRepository participantRepository;
    private CustomEventPublisher customEventPublisher;
    private MessageSource messageSource;


    public SignupEvent getEventForSignUp(String eventId, Locale usersLocale, ZoneId userTimeZone) {
        Event event = eventService.getEvent(eventId, usersLocale);
        Instant now = Instant.now();
        if (event.getSignupStarts() != null && event.getSignupStarts().isAfter(now)) {
            ZonedDateTime zonedDateTime = ZonedDateTime.from(event.getSignupStarts().atZone(userTimeZone));
            String formattedZonedDateTime = zonedDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            String errorMsg = String.format(messageSource.getMessage("signup.not.started.error", null, usersLocale),
                event.getName(), formattedZonedDateTime);
            throw new SignupNotStartedException(errorMsg);
        }
        if (event.getSignupEnds() != null && event.getSignupEnds().isBefore(now)) {
            throw new SignupEndedException(String.format(messageSource.getMessage("signup.ended.error", null, usersLocale),
                event.getName()));
        }
        if (event.getStartDate().isBefore(now)) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("signup.event.already.held.error", null, usersLocale),
                event.getName()));
        }
        if (event.getMaxParticipants() != null) {
            long numOfParticipants = participantRepository.countAllByEvent(eventId);
            if (numOfParticipants >= event.getMaxParticipants()) {
                throw new EventFullException(String.format(messageSource.getMessage("signup.event.full.error", null, usersLocale),
                    event.getName()));
            }
        }
        return new SignupEvent(event);
    }

    public Participant addParticipantToEvent(String eventId, Participant participant, Locale usersLocale, ZoneId userTimeZone) {
        if (!eventId.equals(participant.getEvent())) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), participant.getEvent()));
        }
        if (!eventService.eventExists(eventId)) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), eventId));
        }
        participant.setSignupTime(Instant.now());
        participant = participantRepository.save(participant);
        customEventPublisher.publishSignupSuccessfulEvent(eventService.getEvent(eventId, usersLocale), participant, usersLocale, userTimeZone);
        return participant;
    }

    public void removeParticipantFromEvent(String eventId, String participantId, Locale usersLocale, ZoneId userTimeZone) {
        if (!eventService.eventExists(eventId)) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), eventId));
        }
        Participant participant = participantRepository.findById(participantId).orElseThrow(() ->
            new ParticipantNotFoundException(String.format(messageSource.getMessage("signup.participant.remove.error", null, usersLocale), participantId, eventId)));
        participantRepository.deleteParticipantByEventAndId(eventId, participantId);
        customEventPublisher.publishSignupCancelledEvent(eventService.getEvent(eventId, usersLocale), participant, usersLocale, userTimeZone);
    }
}
