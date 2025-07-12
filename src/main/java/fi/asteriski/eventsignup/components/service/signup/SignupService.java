/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.service.signup;

import fi.asteriski.eventsignup.components.dao.EventDao;
import fi.asteriski.eventsignup.components.dto.EventDto;
import fi.asteriski.eventsignup.components.dto.FormDto;
import fi.asteriski.eventsignup.components.service.EventService;
import fi.asteriski.eventsignup.components.service.FormService;
import java.util.UUID;

import fi.asteriski.eventsignup.components.service.ParticipantService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fi.asteriski.eventsignup.supporting.utils.Constants.EVENT_NOT_FOUND_EXCEPTION_SUPPLIER;

@Log4j2
@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class SignupService {

    private final EventService eventService;
    private final ParticipantService participantService;
    private final FormService formService;
    private final EventDao eventDao;

//    @Transactional
//    public void signupForAnEvent(final UUID eventId, final ParticipantDto participantDto) {
//        var event = eventService.fetchEventForSignupById(eventId);
//        var participant = ParticipantEntity.builder()
//                .userEmail(participantDto.userEmail())
//                .id(participantDto.id())
//                .answers(participantDto.answers())
//                .build();
//        event.addParticipant(participant);
//        eventService.save(event);
//    }

    public EventDto fetchSignupEvent(final UUID eventId) {
        return eventService.fetchEventById(eventId);
    }

//    public ParticipantDto fetchParticipants(final UUID formId) {
//        return eventService.fetchEventById(formId);
//    }

    public EventDto fetchEventById(@NotNull final UUID eventId) {
        return eventDao.fetchEventById(eventId).orElseThrow(EVENT_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    public FormDto fetchSignupForm(final UUID formId) {
        return formService.fetchForm(formId);
    }
}
