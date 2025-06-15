/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.event;

import static fi.asteriski.eventsignup.utils.Utils.getUserName;

import fi.asteriski.eventsignup.dao.EventDao;
import fi.asteriski.eventsignup.dao.entity.EventEntity;
import fi.asteriski.eventsignup.dao.entity.FormEntity;
import fi.asteriski.eventsignup.dto.*;
import fi.asteriski.eventsignup.exception.EventNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventDao eventDao;
    private final FormService formService;

    @Transactional
    public void createNewEvent(@NotNull final EventDto eventDto) {
        eventDao.createNewEvent(eventDto);
    }

    public EventsDto fetchEvents() {
        var newEvents = eventDao.fetchNewestEvents();
        var upcomingEvents = eventDao.fetchUpcomingEvents();
        var pastEvents = eventDao.fetchPastEvents();
        var events = MyEvents.builder()
                .newEvents(newEvents.stream().map(EventEntity::toDto).toList())
                .upcomingEvents(upcomingEvents.stream().map(EventEntity::toDto).toList())
                .pastEvents(pastEvents.stream().map(EventEntity::toDto).toList())
                .build();
        var forms = MyForms.builder()
                .newEventsForms(newEvents.stream()
                        .map(EventEntity::getForm)
                        .map(FormEntity::toDto)
                        .toList())
                .upcomingEventsForms(upcomingEvents.stream()
                        .map(EventEntity::getForm)
                        .map(FormEntity::toDto)
                        .toList())
                .pastEventsForms(pastEvents.stream()
                        .map(EventEntity::getForm)
                        .map(FormEntity::toDto)
                        .toList())
                .build();

        return EventsDto.builder().myEvents(events).myForms(forms).build();
    }

    @Transactional
    public void updateEvent(@NotNull final EventDto eventDto) {
        eventDao.updateEvent(eventDto);
    }

    public UsersEvents fetchUsersEvents() {
        var events = eventDao.fetchUsersEvents(getUserName());
        return UsersEvents.builder()
                .myEvents(events)
                .myForms(
                        formService.fetchForms(events.stream().map(EventDto::id).toList()))
                .build();
    }

    public EventDto fetchEventById(UUID eventId) {
        return eventDao.fetchEventById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found."));
    }

    public EventEntity fetchEventForSignupById(UUID eventId) {
        return eventDao.fetchEventForSignup(eventId).orElseThrow(() -> new EventNotFoundException("Event not found."));
    }

    @Transactional
    public void save(EventEntity event) {
        eventDao.save(event);
    }
}
