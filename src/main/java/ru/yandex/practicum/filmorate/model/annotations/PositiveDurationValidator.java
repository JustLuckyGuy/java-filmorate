package ru.yandex.practicum.filmorate.model.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {
    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        if (duration == null) return true;
        return !duration.isNegative() && !duration.isZero();
    }
}
