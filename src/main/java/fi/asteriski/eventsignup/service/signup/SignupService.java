/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.dao.entity.ParticipantEntity;
import fi.asteriski.eventsignup.dto.EventDto;
import fi.asteriski.eventsignup.dto.FormDto;
import fi.asteriski.eventsignup.dto.ParticipantDto;
import fi.asteriski.eventsignup.service.event.EventService;
import fi.asteriski.eventsignup.service.event.FormService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class SignupService {

    private final EventService eventService;
    private final FormService formService;

    @Transactional
    public void signupForAnEvent(final UUID eventId, final ParticipantDto participantDto) {
        var event = eventService.fetchEventForSignupById(eventId);
        var participant = ParticipantEntity.builder()
                .userEmail(participantDto.userEmail())
                .id(participantDto.id())
                .answers(participantDto.answers())
                .build();
        event.addParticipant(participant);
        eventService.save(event);
    }

    public EventDto fetchSignupEvent(final UUID eventId) {
        return eventService.fetchEventById(eventId);
    }

    public FormDto fetchSignupForm(final UUID formId) {
        return formService.fetchForm(formId);
    }
}
