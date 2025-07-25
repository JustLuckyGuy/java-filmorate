package ru.yandex.practicum.filmorate.model.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinDateValidator implements ConstraintValidator<AfterMinDay, LocalDate> {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 18);


    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) return true;
        return !localDate.isBefore(MIN_DATE);
    }
}
