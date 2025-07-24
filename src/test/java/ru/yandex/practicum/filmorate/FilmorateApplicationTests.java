package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Убить билла")
                .description("Фильм про месть")
                .releaseDate(LocalDate.of(2003, 2, 4))
                .duration(Duration.ofMinutes(111))
                .build();

        user = User.builder()
                .email("today@mail.ru")
                .login("today")
                .birthday(LocalDate.now())
                .build();
    }

    @Test
    void createFilmShouldReturn200Status() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());
    }


    @Test
    void createFilmShouldReturn400StatusWhenDescriptionHasMore200Symbols() throws Exception {
        film.setDescription("Описание".repeat(200));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmShouldReturn400StatusWhenNameIsBlank() throws Exception {
        film.setName("");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmShouldReturn400StatusWhenDateReleaseIsEarlier1895Year() throws Exception {
        film.setReleaseDate(LocalDate.of(1850, 11, 2));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmShouldReturn400StatusWhenDurationIsNegative() throws Exception {
        film.setDuration(Duration.ofMinutes(-100));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmShouldReturn400StatusWhenDurationEqualsZero() throws Exception {
        film.setDuration(Duration.ZERO);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmShouldReturn200Status() throws Exception {
        film.setId(1L);
        film.setName("Интерстеллар");
        film.setDescription("Фантастика");

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(film.getId()))
                .andExpect(jsonPath("$.name").value("Интерстеллар"))
                .andExpect(jsonPath("$.description").value("Фантастика"));
    }

    @Test
    void createUserShouldReturn200Status() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }


    @Test
    void updateUserShouldReturn400StatusWhenLoginWithSpace() throws Exception {
        user.setLogin("new login with space");
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserShouldReturn400StatusWhenBirthdayInFuture() throws Exception {
        user.setBirthday(user.getBirthday().plusMonths(5));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserShouldReturn400StatusWhenEmailIsIncorrect() throws Exception {
        user.setEmail("myEmail");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

}