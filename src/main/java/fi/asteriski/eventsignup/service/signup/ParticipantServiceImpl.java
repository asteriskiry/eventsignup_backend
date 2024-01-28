/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.signup;

import fi.asteriski.eventsignup.dao.signup.ParticipantDao;
import fi.asteriski.eventsignup.domain.signup.ParticipantDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    @NonNull
    private ParticipantDao participantDao;

    @Override
    public long countAllByEvent(String eventId) {
        return participantDao.countAllByEvent(eventId);
    }

    @Override
    public void deleteAllByEventIn(List<String> eventIds) {
        participantDao.deleteAllByEventIds(eventIds);
    }

    @Override
    public List<ParticipantDto> findAllByEvent(String eventId){
        return participantDao.findAllByEvent(eventId);
    }

    @Override
    public void deleteAllByEvent(String eventId) {
        participantDao.deleteAllByEvent(eventId);
    }

    @Override
    public ParticipantDto save(ParticipantDto participantDto) {
        return participantDao.save(participantDto);
    }

    @Override
    public Optional<ParticipantDto> findById(String participantId) {
        return participantDao.findById(participantId);
    }

    @Override
    public void deleteParticipantByEventAndId(String eventId, String participantId) {
        participantDao.deleteParticipantByEventAndId(eventId, participantId);
    }

    @Override
    public List<ParticipantDto> findAll() {
        return participantDao.findAll();
    }
}
