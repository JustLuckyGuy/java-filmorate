package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
public class User {
    @Min(1)
    private Long id;
    @NotBlank
    @Email(message = "Неправильно введен email")
    private String email;
    @NotBlank(message = "Поле должно быть заполнено")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения указана в будущем")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}
