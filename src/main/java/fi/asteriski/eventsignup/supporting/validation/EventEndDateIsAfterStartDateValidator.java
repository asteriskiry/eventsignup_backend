/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUPL-1.2 or later.
 */
package fi.asteriski.eventsignup.supporting.validation;

import fi.asteriski.eventsignup.components.dto.EventDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventEndDateIsAfterStartDateValidator
        implements ConstraintValidator<EventEndDateIsAfterStartDay, EventDto> {

    @Override
    public boolean isValid(EventDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.endDate() == null) {
            return true;
        }
        return value.endDate().isAfter(value.startDate());
    }
}
