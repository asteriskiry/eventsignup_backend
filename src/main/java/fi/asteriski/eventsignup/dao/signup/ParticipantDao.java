/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.signup;

import fi.asteriski.eventsignup.ParticipantRepository;
import fi.asteriski.eventsignup.domain.signup.Participant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ParticipantDao {
    @NonNull
    private ParticipantRepository participantRepository;

    public long countAllByEvent(@NotNull final String eventId) {
        return participantRepository.countAllByEvent(eventId);
    }

    public void deleteAllByEventId(@NotNull final List<String> eventIds) {
        participantRepository.deleteAllByEventIn(eventIds);
    }

    public List<Participant> findAllByEvent(@NotNull final String eventId) {
        return participantRepository.findAllByEvent(eventId);
    }

    public void deleteAllByEvent(String eventId) {
        participantRepository.deleteAllByEvent(eventId);
    }
}
