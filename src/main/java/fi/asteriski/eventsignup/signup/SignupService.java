/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.signup;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.SignupEvent;
import fi.asteriski.eventsignup.domain.event.EventDto;
import fi.asteriski.eventsignup.domain.event.EventEntity;
import fi.asteriski.eventsignup.domain.signup.Participant;
import fi.asteriski.eventsignup.exception.*;
import fi.asteriski.eventsignup.repo.event.EventRepository;
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
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Service
public class SignupService {

    private EventService eventService;
    private ParticipantRepository participantRepository;
    private EventRepository eventRepository;
    private CustomEventPublisher customEventPublisher;
    private MessageSource messageSource;


    public SignupEvent getEventForSignUp(String eventId, Locale usersLocale, ZoneId userTimeZone) {
        var eventDto = eventService.getEvent(eventId, usersLocale, Optional.empty());
        ZonedDateTime now = ZonedDateTime.now();
        if (eventDto.getSignupStarts() != null && eventDto.getSignupStarts().isAfter(now)) {
            String formattedZonedDateTime = eventDto.getSignupStarts().format(DateTimeFormatter.RFC_1123_DATE_TIME);
            String errorMsg = String.format(messageSource.getMessage("signup.not.started.error", null, usersLocale),
                eventDto.getName(), formattedZonedDateTime);
            throw new SignupNotStartedException(errorMsg);
        }
        if (eventDto.getSignupEnds() != null && eventDto.getSignupEnds().isBefore(now)) {
            throw new SignupEndedException(String.format(messageSource.getMessage("signup.ended.error", null, usersLocale),
                eventDto.getName()));
        }
        if (eventDto.getStartDate().isBefore(now)) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("signup.event.already.held.error", null, usersLocale),
                eventDto.getName()));
        }
        if (eventDto.getMaxParticipants() != null) {
            long numOfParticipants = participantRepository.countAllByEvent(eventId);
            if (numOfParticipants >= eventDto.getMaxParticipants()) {
                throw new EventFullException(String.format(messageSource.getMessage("signup.event.full.error", null, usersLocale),
                    eventDto.getName()));
            }
        }
        return eventDto.toSignupEvent();
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
        customEventPublisher.publishSignupSuccessfulEvent(eventService.getEvent(eventId, usersLocale, Optional.empty()), participant, usersLocale, userTimeZone);
        return participant;
    }

    public void removeParticipantFromEvent(String eventId, String participantId, Locale usersLocale, ZoneId userTimeZone) {
        if (!eventService.eventExists(eventId)) {
            throw new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), eventId));
        }
        Participant participant = participantRepository.findById(participantId).orElseThrow(() ->
            new ParticipantNotFoundException(String.format(messageSource.getMessage("signup.participant.remove.error", null, usersLocale), participantId, eventId)));
        participantRepository.deleteParticipantByEventAndId(eventId, participantId);
        customEventPublisher.publishSignupCancelledEvent(eventService.getEvent(eventId, usersLocale, Optional.empty()), participant, usersLocale, userTimeZone);
    }

    public List<SignupEvent> getUpcomingEvents(String days) {
        var today = Instant.now();
        var daysToGet = Integer.parseInt(days);
        return eventRepository.findAllByStartDateIsBetween(today, today.plus(daysToGet, ChronoUnit.DAYS)).stream()
            .map(EventEntity::toDto)
            .map(EventDto::toSignupEvent)
            .toList();
    }
}
