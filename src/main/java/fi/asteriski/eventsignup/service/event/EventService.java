/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.dao.event.EventDao;
import fi.asteriski.eventsignup.dto.EventDto;
import fi.asteriski.eventsignup.dto.MyEvents;
import fi.asteriski.eventsignup.dto.UsersEvents;
import fi.asteriski.eventsignup.utils.CustomEventPublisher;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventService {

    private EventDao eventDao;
    private CustomEventPublisher customEventPublisher;
    private MessageSource messageSource;

    @Transactional
    public void createNewEvent(@NotNull final EventDto eventDto) {}

    public MyEvents fetchEvents() {
        return null;
    }

    @Transactional
    public void updateEvent(@NotNull final EventDto eventDto) {}

    public UsersEvents fetchUsersEvents() {
        return null;
    }

    //    @Override
    //    public EventDto getEvent(
    //            UUID id, Locale usersLocale, Optional<Supplier<? extends EventSignupException>> errorSupplier) {
    //        Supplier<EventNotFoundException> defaultErrorSupplier = () -> new EventNotFoundException(
    //                String.format(messageSource.getMessage("event.not.found.message", null, usersLocale), id));
    //
    //        return eventDao.findById(id).orElseThrow(errorSupplier.orElse(defaultErrorSupplier));
    //    }
    //
    //    @Override
    //    public List<EventDto> getAllEventsForUser(String user) {
    //        return eventDao.findAllByOwner(user);
    //    }
    //
    //    @Override
    //    public List<ParticipantDto> getParticipants(UUID eventId) {
    //        return participantService.findAllByEvent(eventId);
    //    }
    //
    //    @Override
    //    @Transactional
    //    public EventDto createNewEvent(EventDto eventDto, Locale usersLocale, ZoneId userTimeZone) {
    //        var authentication = SecurityContextHolder.getContext().getAuthentication();
    //        eventDto.setOwner(authentication.getName());
    //        if (StringUtils.hasText(eventDto.getBannerImg())) {
    //            eventDto.setBannerImg(String.format("%s_%s", authentication.getName(), eventDto.getBannerImg()));
    //        }
    //        if (eventDto.getForm().getUserCreated() == null) {
    //            eventDto.getForm().setUserCreated(authentication.getName());
    //        }
    //        if (eventDto.getForm().getDateCreated() == null) {
    //            eventDto.getForm().setDateCreated(Instant.now());
    //        }
    //        customEventPublisher.publishSavedEventEvent(eventDto, authentication, usersLocale, userTimeZone);
    //        return eventDao.save(eventDto);
    //    }
    //
    //    @Override
    //    @Transactional
    //    public EventDto editExistingEvent(EventDto newEventDto, Locale usersLocale, ZoneId userTimeZone) {
    //        var authentication = SecurityContextHolder.getContext().getAuthentication();
    //        var oldEventDto = eventDao.findById(newEventDto.getId()).orElseThrow(() -> {
    //            log.error(String.format(
    //                    "%s Unable to edit existing event. Old event with id <%s> was not found!",
    //                    LOG_PREFIX, newEventDto.getId()));
    //            return new EventNotFoundException(newEventDto.getId().toString());
    //        });
    //        newEventDto.setId(oldEventDto.getId());
    //        customEventPublisher.publishSavedEventEvent(newEventDto, authentication, usersLocale, userTimeZone);
    //        return eventDao.save(newEventDto);
    //    }
    //
    //    @Override
    //    @Transactional
    //    public void removeEventAndParticipants(UUID eventId) {
    //        eventDao.deleteById(eventId);
    //        participantService.deleteAllByEvent(eventId);
    //    }
    //
    //    @Override
    //    public boolean eventExists(UUID eventId) {
    //        return eventDao.existsById(eventId);
    //    }
    //
    //    @Override
    //    public List<EventDto> findAllByStartDateIsBeforeOrEndDateIsBefore(Instant dateLimit, Instant dateLimit1) {
    //        return eventDao.findAllByStartDateIsBeforeOrEndDateIsBefore(dateLimit, dateLimit1);
    //    }
    //
    //    @Override
    //    @Transactional
    //    public void deleteAllByIds(List<UUID> eventIds) {
    //        eventDao.deleteAllByIds(eventIds);
    //    }
    //
    //    @Override
    //    public List<EventDto> findAllByStartDateIsBetween(Instant date1, Instant date2) {
    //        return eventDao.findAllByStartDateIsBetween(date1, date2);
    //    }
    //
    //    @Override
    //    public List<EventDto> findAll() {
    //        return eventDao.findAll();
    //    }
}
