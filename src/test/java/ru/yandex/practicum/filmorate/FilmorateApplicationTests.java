package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private FilmService filmService;

    private Film film;
    private User user;

//    @BeforeEach
//    void setUp() {
//        film = Film.builder()
//                .id(1L)
//                .name("Убить билла")
//                .description("Фильм про месть")
//                .releaseDate(LocalDate.of(2003, 2, 4))
//                .duration(111L)
//                .build();
//
//        user = User.builder()
//                .id(1L)
//                .email("today@mail.ru")
//                .login("today")
//                .birthday(LocalDate.now())
//                .build();
//        userService.createUser(user);
//        filmService.createFilm(film);
//    }

    @Test
    void contextLoad(ApplicationContext app) {
        assertNotNull(app);
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
        film.setDuration(-1L);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmShouldReturn400StatusWhenDurationEqualsZero() throws Exception {
        film.setDuration(0L);
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
                .andDo(print()) // Для отладки
                .andExpect(status().isOk());
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

    @Test
    void getPopularShouldReturn200Status() throws Exception {
        mockMvc.perform(get("/films/popular")
                        .param("count", "10"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void setLikeShouldReturn200Status() throws Exception {

        mockMvc.perform(put("/films/{filmId}/like/{userId}", 1L, 1L))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void removeLikeShouldReturn200Status() throws Exception {
        mockMvc.perform(delete("/films/{0}/like/{1}", "1", "2"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getUserFriendsShouldReturn200AndReturnsFriends() throws Exception {

        when(userService.showAllFriend(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getUserFriendsShouldReturn500Status() throws Exception {
        mockMvc.perform(get("/users/friends"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getCommonFriendsShouldReturn200StatusAndListOfFriends() throws Exception {
        when(userService.similarFriends(1L, 2L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getCommonFriendsShouldReturn500Status() throws Exception {
        mockMvc.perform(get("/users/null/friends/common/2"))
                .andExpect(status().is5xxServerError());
    }


    @Test
    void addFriendShouldReturn200StatusAndReturnsUser() throws Exception {
        User newUser = User.builder()
                .id(1L)
                .login("Kirill")
                .email("example@mail.ru")
                .build();
        when(userService.addFriend(1L, 2L)).thenReturn(newUser);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("example@mail.ru"));
    }

    @Test
    void addFriendShouldReturn500Status() throws Exception {
        mockMvc.perform(put("/users/null/friends/2"))
                .andExpect(status().is5xxServerError());

    }


    @Test
    void removeFriendShouldReturn200Status() throws Exception {
        User newUser = User.builder()
                .id(1L)
                .login("Kirill")
                .email("example@mail.ru")
                .build();
        when(userService.removeFriend(1L, 2L)).thenReturn(newUser);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("example@mail.ru"));
    }

    @Test
    void removeFriendShouldReturn500Status() throws Exception {
        mockMvc.perform(delete("/users/null/friends/2"))
                .andExpect(status().is5xxServerError());
    }

}