/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.service.event;

import fi.asteriski.eventsignup.dto.FormDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FormService {
    @Transactional
    public void updateForm(@NotNull final FormDto formDto) {}

    @Transactional
    public void createForm(@NotNull final FormDto formDto) {}
}
