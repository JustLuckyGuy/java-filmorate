package ru.yandex.practicum.filmorate.model.annotations;


import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PositiveDurationValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveDuration {
    String message() default "Продолжительность должна быть положительным значением";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
