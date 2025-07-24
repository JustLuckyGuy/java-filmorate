package ru.yandex.practicum.filmorate.model.annotations;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MinDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterMinDay {
    String message() default "Дата не может быть раньше 18 декабря 1895 года";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
