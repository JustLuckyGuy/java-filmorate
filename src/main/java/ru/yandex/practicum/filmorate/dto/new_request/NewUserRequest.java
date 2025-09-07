package ru.yandex.practicum.filmorate.dto.new_request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;


@Data
public class NewUserRequest {
    @NotBlank
    @Email(message = "Неправильно введен email")
    private String email;
    @NotBlank(message = "Поле должно быть заполнено")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения указана в будущем")
    private LocalDate birthday;
}
