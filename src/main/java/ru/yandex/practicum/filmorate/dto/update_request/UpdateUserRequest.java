package ru.yandex.practicum.filmorate.dto.update_request;


import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private String email;
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения указана в будущем")
    private LocalDate birthday;

    public boolean hasEmail(){
        return ! (email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}
