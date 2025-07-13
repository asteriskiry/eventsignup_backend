package fi.asteriski.eventsignup.components.service;

import fi.asteriski.eventsignup.components.dao.ParticipantDao;
import fi.asteriski.eventsignup.components.entity.FormEntity;
import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import fi.asteriski.eventsignup.components.dao.repository.ParticipantRepository;
import fi.asteriski.eventsignup.components.dto.ParticipantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantDao participantDao;
    private final FormService formService;

    public List<ParticipantDto> getAllByFormId(UUID formId) {
        return participantRepository.findAllByFormId(formId)
            .stream()
            .map(ParticipantEntity::toDto)
            .toList();
    }

    @Transactional
    public ParticipantDto addParticipant(UUID formId, ParticipantDto participantDto) {
        System.out.println("ASDAR:::");
        System.out.println(participantDto);
        // 1. fetch the FormEntity (or getReference for the FK proxy)
        FormEntity form = formService.fetchForm(formId).toEntity();

        // 2. map → ParticipantEntity
        ParticipantEntity entity = participantDto.toEntity(form);

        // 3. persist and return DTO
        ParticipantEntity saved = participantRepository.save(entity);
        return saved.toDto();
    }

    @Transactional
    public void save(ParticipantDto participant) {
        participantDao.save(participant);
    }
}
