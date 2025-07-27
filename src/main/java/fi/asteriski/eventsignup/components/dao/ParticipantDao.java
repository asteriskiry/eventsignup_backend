/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.components.dao;

import fi.asteriski.eventsignup.components.dao.repository.ParticipantRepository;
import fi.asteriski.eventsignup.components.dto.ParticipantDto;
import fi.asteriski.eventsignup.components.entity.FormEntity;
import fi.asteriski.eventsignup.components.entity.ParticipantEntity;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipantDao {

    private final ParticipantRepository participantRepository;
    private final FormDao formDao;

    public ParticipantDto save(ParticipantDto dto) {
        // 1. retrieve the form from DB (or get a JPA proxy)
        FormEntity form = formDao.fetchFormEntity(dto.formId());

        // 2. convert DTO → Entity
        ParticipantEntity entity = dto.toEntity(form);

        // 3. save and map back to DTO
        ParticipantEntity saved = participantRepository.save(entity);
        return saved.toDto();
    }

    public ParticipantDto addParticipant(UUID formId, ParticipantDto participantDto) {
        FormEntity form = formDao.fetchFormEntity(formId);

        // 2. map → ParticipantEntity
        ParticipantEntity entity = participantDto.toEntity(form);

        // 3. persist and return DTO
        ParticipantEntity saved = participantRepository.save(entity);
        return saved.toDto();
    }
}
