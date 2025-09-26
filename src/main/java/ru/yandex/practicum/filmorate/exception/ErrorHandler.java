package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final String ERROR = "error";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidated(final ValidationException e) {
        log.error("Произошла ошибка в валидации {}", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidate(final MethodArgumentNotValidException e) {
        log.error("Возникла ошибка в валидации свойства {}", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log.error("Не был найден объект {}", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleException(final Exception e) {
        log.error("Возникла ошибка! {}", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDataAccessException(final DataIntegrityViolationException e) {
        log.error("Пользователь пытался добавить в друзья дважды {}", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleDataAccessException(final DuplicateDataException e) {
        log.error("Пользователь дублирует данные {}", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }


}
