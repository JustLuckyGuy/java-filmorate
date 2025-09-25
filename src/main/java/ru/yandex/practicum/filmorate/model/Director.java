package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Director {
    private Long id;
    @NotBlank(message = "Поле должно быть заполнено")
    @Size(max = 256, message = "Превышен лимит символов")
    private String name;
}
