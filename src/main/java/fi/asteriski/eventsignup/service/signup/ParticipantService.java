/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.dao.signup.ParticipantDao;
import fi.asteriski.eventsignup.domain.signup.Participant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    @NonNull
    private ParticipantDao participantDao;

    public long countAllByEvent(String eventId) {
        return participantDao.countAllByEvent(eventId);
    }

    public void deleteAllByEventIn(List<String> eventIds) {
        participantDao.deleteAllByEventId(eventIds);
    }

    public List<Participant> findAllByEvent(String eventId){
        return participantDao.findAllByEvent(eventId);
    }

    public void deleteAllByEvent(String eventId) {
        participantDao.deleteAllByEvent(eventId);
    }
}
