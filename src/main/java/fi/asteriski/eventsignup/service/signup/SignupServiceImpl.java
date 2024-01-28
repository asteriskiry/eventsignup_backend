/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.exception.*;
import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.model.signup.SignupEvent;
import fi.asteriski.eventsignup.service.event.EventService;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static fi.asteriski.eventsignup.utils.Constants.UTC_TIME_ZONE;

@Log4j2
@AllArgsConstructor
@Service
public class SignupServiceImpl implements SignupService {

    private EventService eventService;
    private ParticipantService participantService;
    private CustomEventPublisher customEventPublisher;
    private MessageSource messageSource;


    @Override
    public SignupEvent getEventForSignUp(String eventId, Locale usersLocale, ZoneId userTimeZone) {
        var event = eventService.getEvent(eventId, usersLocale, Optional.empty());
        var now = ZonedDateTime.now(UTC_TIME_ZONE);
        if (event.getSignupStarts() != null && event.getSignupStarts().isAfter(now)) {
            var formattedZonedDateTime = event.getSignupStarts().format(DateTimeFormatter.RFC_1123_DATE_TIME);
            var errorMsg = String.format(messageSource.getMessage("signup.not.started.error", null, usersLocale),
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
            long numOfParticipants = participantService.countAllByEvent(eventId);
            if (numOfParticipants >= event.getMaxParticipants()) {
                throw new EventFullException(String.format(messageSource.getMessage("signup.event.full.error", null, usersLocale),
                    event.getName()));
            }
        }
        return event.toSignupEvent();
    }

    @Override
    public ParticipantDto addParticipantToEvent(String eventId, ParticipantDto participant, Locale usersLocale, ZoneId userTimeZone) {
        if (!Objects.equals(eventId, participant.getEvent())) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), participant.getEvent()));
        }
        if (!eventService.eventExists(eventId)) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), eventId));
        }
        participant.setSignupTime(Instant.now());
        participant = participantService.save(participant);
        customEventPublisher.publishSignupSuccessfulEvent(eventService.getEvent(eventId, usersLocale, Optional.empty()), participant, usersLocale, userTimeZone);
        return participant;
    }

    @Override
    public void removeParticipantFromEvent(String eventId, String participantId, Locale usersLocale, ZoneId userTimeZone) {
        if (!eventService.eventExists(eventId)) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), eventId));
        }
        var participantDto = participantService.findById(participantId).orElseThrow(() ->
            new ParticipantNotFoundException(String.format(messageSource.getMessage("signup.participant.remove.error", null, usersLocale), participantId, eventId)));
        participantService.deleteParticipantByEventAndId(eventId, participantId);
        customEventPublisher.publishSignupCancelledEvent(eventService.getEvent(eventId, usersLocale, Optional.empty()), participantDto, usersLocale, userTimeZone);
    }

    @Override
    public List<SignupEvent> getUpcomingEvents(String days) {
        var today = Instant.now();
        var daysToGet = Integer.parseInt(days);
        return eventService.findAllByStartDateIsBetween(today, today.plus(daysToGet, ChronoUnit.DAYS)).stream()
            .map(EventDto::toSignupEvent)
            .toList();
    }
}
