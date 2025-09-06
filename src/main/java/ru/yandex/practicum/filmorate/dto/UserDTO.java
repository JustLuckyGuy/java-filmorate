package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String login;
    private LocalDate birthday;
}
