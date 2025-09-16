package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewRequest {
    private Integer reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва обязателен")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "ID фильма обязателен")
    private Long filmId;

    private Integer useful;
}