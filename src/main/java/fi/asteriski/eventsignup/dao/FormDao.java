/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.dao;

import fi.asteriski.eventsignup.dto.FormDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FormDao {

    public List<FormDto> fetchForms(@NotNull List<UUID> formIds) {
        return null;
    }

    public void createForm(@NotNull final FormDto formDto) {}

    public void updateForm(@NotNull final FormDto formDto) {}
}
