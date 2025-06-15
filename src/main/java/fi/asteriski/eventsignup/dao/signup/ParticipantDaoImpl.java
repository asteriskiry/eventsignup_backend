/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao.signup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipantDaoImpl implements ParticipantDao {
    //    @NonNull
    //    private ParticipantRepository participantRepository;
    //
    //    @Override
    //    public long countAllByEvent(@NotNull final UUID eventId) {
    //        return participantRepository.countAllByEvent(eventId);
    //    }
    //
    //    @Override
    //    public void deleteAllByEventIds(@NotNull final List<UUID> eventIds) {
    //        participantRepository.deleteAllByEventIn(eventIds);
    //    }
    //
    //    @Override
    //    public List<ParticipantDto> findAllByEvent(@NotNull final UUID eventId) {
    //        return participantRepository.findAllByEvent(eventId).stream()
    //                .map(ParticipantEntity::toDto)
    //                .toList();
    //    }
    //
    //    @Override
    //    public void deleteAllByEvent(@NotNull final UUID eventId) {
    //        participantRepository.deleteAllByEvent(eventId);
    //    }
    //
    //    @Override
    //    public ParticipantDto save(@NotNull final ParticipantDto participantDto) {
    //        return participantRepository.save(participantDto.toEntity()).toDto();
    //    }
    //
    //    @Override
    //    public Optional<ParticipantDto> findById(UUID participantId) {
    //        return participantRepository.findById(participantId).map(ParticipantEntity::toDto);
    //    }
    //
    //    @Override
    //    public void deleteParticipantByEventAndId(UUID eventId, UUID participantId) {
    //        participantRepository.deleteParticipantByEventAndId(eventId, participantId);
    //    }
    //
    //    @Override
    //    public List<ParticipantDto> findAll() {
    //        return participantRepository.findAll().stream()
    //                .map(ParticipantEntity::toDto)
    //                .toList();
    //    }
}
