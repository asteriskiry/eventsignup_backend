/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.dao.event.EventDao;
import fi.asteriski.eventsignup.exception.EventNotFoundException;
import fi.asteriski.eventsignup.exception.EventSignupException;
import fi.asteriski.eventsignup.model.event.EventDto;
import fi.asteriski.eventsignup.model.signup.ParticipantDto;
import fi.asteriski.eventsignup.service.signup.ParticipantService;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

@Log4j2
@AllArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private static final String LOG_PREFIX = "[EventServiceImpl]";

    private EventDao eventDao;
    private ParticipantService participantService;
    private CustomEventPublisher customEventPublisher;
    private MessageSource messageSource;

    @Override
    public EventDto getEvent(String id, Locale usersLocale, Optional<Supplier<? extends EventSignupException>> errorSupplier) {
        Supplier<EventNotFoundException> defaultErrorSupplier = () ->
            new EventNotFoundException(String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), id));

        return eventDao.findById(id).orElseThrow(errorSupplier.orElse(defaultErrorSupplier));
    }

    @Override
    public List<EventDto> getAllEventsForUser(String user) {
        return eventDao.findAllByOwner(user);
    }

    @Override
    public List<ParticipantDto> getParticipants(String eventId) {
        return participantService.findAllByEvent(eventId);
    }

    @Override
    public EventDto createNewEvent(EventDto eventDto, Locale usersLocale, ZoneId userTimeZone) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        eventDto.setOwner(authentication.getName());
        if (StringUtils.hasText(eventDto.getBannerImg())) {
            eventDto.setBannerImg(String.format("%s_%s", authentication.getName(), eventDto.getBannerImg()));
        }
        if (eventDto.getForm().getUserCreated() == null) {
            eventDto.getForm().setUserCreated(authentication.getName());
        }
        if (eventDto.getForm().getDateCreated() == null) {
            eventDto.getForm().setDateCreated(Instant.now());
        }
        customEventPublisher.publishSavedEventEvent(eventDto, authentication, usersLocale, userTimeZone);
        return eventDao.save(eventDto);
    }

    @Override
    public EventDto editExistingEvent(EventDto newEventDto, Locale usersLocale, ZoneId userTimeZone) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var oldEventDto = eventDao.findById(newEventDto.getId()).orElseThrow(() -> {
            log.error(String.format("%s Unable to edit existing event. Old event with id <%s> was not found!", LOG_PREFIX, newEventDto.getId()));
            return new EventNotFoundException(newEventDto.getId());
        });
        newEventDto.setId(oldEventDto.getId());
        customEventPublisher.publishSavedEventEvent(newEventDto, authentication, usersLocale, userTimeZone);
        return eventDao.save(newEventDto);
    }

    @Override
    public void removeEventAndParticipants(String eventId) {
        eventDao.deleteById(eventId);
        participantService.deleteAllByEvent(eventId);
    }

    @Override
    public boolean eventExists(String eventId) {
        return eventDao.existsById(eventId);
    }

    @Override
    public List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1) {
        return eventDao.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit1);
    }

    @Override
    public void deleteAllByIds(List<String> eventIds) {
        eventDao.deleteAllByIds(eventIds);
    }

    @Override
    public List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2) {
        return eventDao.findAllByStartDateIsBetween(date1, date2);
    }

    @Override
    public List<EventDto> findAll() {
        return eventDao.findAll();
    }
}
