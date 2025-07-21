/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.service;

import fi.asteriski.eventsignup.components.dao.EventDao;
import fi.asteriski.eventsignup.components.entity.EventEntity;
import fi.asteriski.eventsignup.components.entity.FormEntity;
import fi.asteriski.eventsignup.components.dto.*;
import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static fi.asteriski.eventsignup.supporting.utils.Constants.EVENT_NOT_FOUND_EXCEPTION_SUPPLIER;
import static fi.asteriski.eventsignup.supporting.utils.Utils.getUserName;

@Log4j2
@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventDao eventDao;
    private final FormService formService;

    @Transactional
    public void createNewEvent(final @Valid NewEventAndFormRequest eventDto) {
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

        //Eventtilistoja vastaavat formit
        var newForms = newEvents.stream().flatMap(event -> event.getForms().stream()).toList();
        var upcomingForms = upcomingEvents.stream().flatMap(event -> event.getForms().stream()).toList();
        var pastForms = pastEvents.stream().flatMap(event -> event.getForms().stream()).toList();
        var forms = MyForms.builder()
                .newEventsForms(
                    newEvents.stream()
                        .flatMap(event -> event.getForms().stream())  // Stream<FormEntity>
                        .map(FormEntity::toDto)                       // Stream<FormDto>
                        .toList())                                      // List<FormDto>
                .upcomingEventsForms(
                    upcomingEvents.stream()
                        .flatMap(event -> event.getForms().stream())
                        .map(FormEntity::toDto)
                        .toList())
                .pastEventsForms(pastEvents.stream()
                        .flatMap(event -> event.getForms().stream())
                        .map(FormEntity::toDto)
                        .toList())
                .build();

        //Formeja vastaavat participantit
        var newParticipants = newForms.stream()
            .flatMap(form -> form.getParticipants().stream())
            .map(ParticipantEntity::toDto).toList();
        var upcomingParticipants = upcomingForms.stream()
            .flatMap(form -> form.getParticipants().stream())
            .map(ParticipantEntity::toDto).toList();
        var pastParticipants = pastForms.stream()
            .flatMap(form -> form.getParticipants().stream())
            .map(ParticipantEntity::toDto).toList();
        var participants = MyParticipants.builder()
            .newParticipants(newParticipants)
            .upcomingParticipants(upcomingParticipants)
            .pastParticipants(pastParticipants).build();

        return EventsDto.builder().myEvents(events).myForms(forms).myParticipants(participants).build();
    }

    @Transactional
    public EventDto updateEvent(@NotNull final EventDto eventDto) {
        return eventDao.updateEvent(eventDto);
    }

//    public UsersEvents fetchUsersEventsNForms() {
//        var events = eventDao.fetchUsersEvents(getUserName());
//        return UsersEvents.builder()
//                .myEvents(events)
//                .myForms(
//                        formService.fetchForms(events.stream().map(EventDto::id).toList()))
//                .build();
//    }

    public UsersEvents fetchUsersEventsNForms() {
        //String user = auth.getCurrentUsername();
        String user = getUserName();
        List<EventDto> events = eventDao.fetchUsersEvents(user);

//        // extract all the event IDs
//        List<UUID> ids = events.stream()
//            .map(EventDto::id)
//            .toList();

        List<FormDto> forms = formService.fetchFormsByEventIds(events.stream().map(EventDto::id).toList());

        return UsersEvents.builder()
            .myEvents(events)
            .myForms(forms)
            .build();
    }

    public EventDto fetchEventById(@NotNull final UUID eventId) {
        return eventDao.fetchEventById(eventId).orElseThrow(EVENT_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    public EventEntity fetchEventForSignupById(@NotNull final UUID eventId) {
        return eventDao.fetchEventEntity(eventId).orElseThrow(EVENT_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    @Transactional
    public void save(EventEntity event) {
        eventDao.save(event);
    }
}
