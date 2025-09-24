package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.*;
import ru.yandex.practicum.filmorate.storage.dao.row_mappers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, GenreRepository.class, MpaRepository.class, FilmRowMapper.class, GenreRowMapper.class, MPARowMapper.class,
        UserDbStorage.class, UserRowMapper.class, DirectorRepository.class, DirectorRowMapper.class})
class FilmDbTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    private Film testFilm;
    private User testUser;


    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .login("testLogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        MPA mpa = new MPA();
        mpa.setId(1L);
        Genre genre = new Genre();
        genre.setId(1L);
        testFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .mpa(mpa)
                .build();
        testFilm.getGenres().add(genre);
    }

    @Test
    void testFindFilmById() {
        Film createdFilm = filmDbStorage.create(testFilm);
        Optional<Film> foundFilm = filmDbStorage.findById(createdFilm.getId());

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(createdFilm.getId());
                    assertThat(film.getName()).isEqualTo("Test Film");
                });
    }

    @Test
    void testFindAllFilms() {
        filmDbStorage.create(testFilm);
        List<Film> films = filmDbStorage.allFilms();

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(1);
    }

    @Test
    void testCreateFilm() {
        Film createdFilm = filmDbStorage.create(testFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("Test Film");
    }

    @Test
    void testUpdateFilm() {
        Film createdFilm = filmDbStorage.create(testFilm);
        UpdateFilmRequest updatedFilm = new UpdateFilmRequest();
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");

        Film result = filmDbStorage.update(FilmMapper.updateFieldsFilms(createdFilm, updatedFilm));

        assertThat(result.getName()).isEqualTo("Updated Film");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testDeleteFilm() {
        Film createdFilm = filmDbStorage.create(testFilm);
        boolean isDeleted = filmDbStorage.delete(createdFilm.getId());

        assertThat(isDeleted).isTrue();
        assertThat(filmDbStorage.findById(createdFilm.getId())).isEmpty();
    }

    @Test
    void testAddAndRemoveLike() {
        User createdUser = userDbStorage.create(testUser);
        Film createdFilm = filmDbStorage.create(testFilm);

        boolean likeAdded = filmDbStorage.addLike(createdFilm.getId(), createdUser.getId());

        boolean likeRemoved = filmDbStorage.removeLike(createdFilm.getId(), createdUser.getId());

        assertThat(likeAdded).isTrue();
        assertThat(likeRemoved).isTrue();
    }

    @Test
    void testPopularFilms() {
        User createdUser = userDbStorage.create(testUser);
        Film createdFilm = filmDbStorage.create(testFilm);
        filmDbStorage.addLike(createdFilm.getId(), createdUser.getId());

        List<Film> popularFilms = filmDbStorage.popularFilms(10, null, null);

        assertThat(popularFilms).isNotNull();
        assertThat(popularFilms.size()).isEqualTo(1L);
        assertThat(popularFilms.getFirst().getId()).isEqualTo(createdFilm.getId());
    }
}

