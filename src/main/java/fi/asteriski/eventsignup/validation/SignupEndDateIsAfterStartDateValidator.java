/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2025.
Licenced under EUPL-1.2 or later.
 */
package fi.asteriski.eventsignup.validation;

import fi.asteriski.eventsignup.dto.EventDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SignupEndDateIsAfterStartDateValidator
        implements ConstraintValidator<SignupEndDateIsAfterStartDay, EventDto> {

    @Override
    public boolean isValid(EventDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.signupEnds() == null) {
            return true;
        }
        return value.signupEnds().isAfter(value.signupStarts());
    }
}
