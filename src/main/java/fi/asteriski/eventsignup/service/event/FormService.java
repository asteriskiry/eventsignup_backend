/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.dao.FormDao;
import fi.asteriski.eventsignup.dto.FormDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FormService {
    private final FormDao formDao;

    @Transactional
    public void updateForm(@NotNull final FormDto formDto) {
        formDao.updateForm(formDto);
    }

    @Transactional
    public void createForm(@NotNull final FormDto formDto) {
        formDao.createForm(formDto);
    }

    public FormDto fetchForm(@NotNull final UUID formId) {
        return fetchForms(List.of(formId)).getFirst();
    }

    public List<FormDto> fetchForms(@NotNull final List<UUID> formIds) {
        return formDao.fetchForms(formIds);
    }
}
