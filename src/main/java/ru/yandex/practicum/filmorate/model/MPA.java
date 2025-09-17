package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MPA {
    private Long id;
    @NotBlank(message = "Поле должно быть заполнено")
    @NotNull(message = "Заполните поле")
    private String name;
}
