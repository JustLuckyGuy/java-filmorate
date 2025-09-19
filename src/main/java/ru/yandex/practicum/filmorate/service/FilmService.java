package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortOrder;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmDb;
    private final UserStorage userStorage;


    @Autowired
    public FilmService(@Qualifier("filmdb") FilmStorage filmDb, @Qualifier("userdb") UserStorage userStorage) {
        this.filmDb = filmDb;
        this.userStorage = userStorage;
    }

    public List<FilmDTO> getAllFilms() {
        log.trace("Произведен вызов всех фильмов из базы данных");
        return filmDb.allFilms().stream().map(FilmMapper::maptoFilmDTO).toList();
    }

    public List<FilmDTO> getFilmsDirector(Long directorId, String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        return filmDb.allFilmsOfDirector(directorId, SortOrder.from(sortBy.toLowerCase())).stream().map(FilmMapper::maptoFilmDTO).toList();
    }

    public FilmDTO findFilmById(long filmId) {
        log.trace("Произведен вызов фильма из базы данных с ID: {}", filmId);
        return filmDb.findById(filmId).map(FilmMapper::maptoFilmDTO).orElseThrow(() -> new NotFoundException("Не удалось найти фильм"));
    }


    public FilmDTO createFilm(NewFilmRequest film) {
        Film createdFilm = FilmMapper.mapToFilm(film);
        createdFilm = filmDb.create(createdFilm);
        log.info("Создан новый фильм с ID: {}.{}", createdFilm.getId(), createdFilm.getName());
        return FilmMapper.maptoFilmDTO(createdFilm);
    }

    public FilmDTO updateFilm(UpdateFilmRequest film) {
        Film film1 = filmDb.findById(film.getId()).map(film2 -> FilmMapper.updateFieldsFilms(film2, film))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        film1 = filmDb.update(film1);
        log.info("Фильм с ID: {}.{} был обновлен", film.getId(), film.getName());
        return FilmMapper.maptoFilmDTO(film1);
    }

    public boolean deleteFilm(long filmId) {
        Film film = searchFilm(filmId);
        log.info("Фильм с ID: {}.{} был удален", film.getId(), film.getName());
        return filmDb.delete(film.getId());
    }

    public List<FilmDTO> getPopularFilm(Integer count, Integer year, Long genreId) {
        if (year != null && year > Year.now().getValue()) {
            throw new ValidationException("Вы не можете запросить фильмы из будущего");
        }
        log.trace("Был произведен вывод популярных фильмов");
        return filmDb.popularFilms(count, year, genreId).stream().map(FilmMapper::maptoFilmDTO).toList();
    }


    public FilmDTO setLike(Long idFilm, Long idUser) {
        checkUser(idUser);
        Film film = searchFilm(idFilm);
        if (film.getLikes().contains(idUser)) {
            throw new ValidationException("Пользователь с id = " + idUser + "уже поставил лайк");
        }
        if (filmDb.addLike(idFilm, idUser)) {
            film.getLikes().add(idUser);
        }
        log.trace("Пользователь с id={} поставил лайк фильму {}", idUser, film.getName());
        return FilmMapper.maptoFilmDTO(film);
    }

    public FilmDTO removeLike(Long idFilm, Long idUser) {
        checkUser(idUser);
        Film film = searchFilm(idFilm);
        if (filmDb.removeLike(idFilm)) {
            film.getLikes().remove(idUser);
        }
        log.trace("Пользователь с id={} убрал лайк фильму {}", idUser, film.getName());
        return FilmMapper.maptoFilmDTO(film);
    }


    private Film searchFilm(Long idFilm) {
        return filmDb.findById(idFilm).orElseThrow(() ->
                new NotFoundException("Не удалось найти фильм"));
    }

    private void checkUser(long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + " не найден");
        }
    }

    public List<FilmDTO> searchFilms(String query, String by) {
        List<FilmDTO> result = new ArrayList<>();
        String searchQuery = query.toLowerCase();
        String[] searchBy = by.split(",");
        boolean byTitle = false;
        boolean byDirector = false;
        for (String searchType : searchBy) {
            switch (searchType.trim().toLowerCase()) {
                case "title":
                    byTitle = true;
                    break;
                case "director":
                    byDirector = true;
                    break;
                default:
                    throw new IllegalArgumentException("Некоректный параметр поиска: " + searchType);
            }
        }
        if (byTitle)
            result = filmDb.searchByTitle(searchQuery).stream().map(FilmMapper::maptoFilmDTO).toList();

        if (byDirector)
            result = filmDb.searchByDirector(searchQuery).stream().map(FilmMapper::maptoFilmDTO).toList();

        if (byTitle && byDirector) {
            List<Film> byTitlelist = filmDb.searchByTitle(searchQuery);
            List<Film> byDirectorList = filmDb.searchByDirector(searchQuery);

            Map<Long, Film> filmsMap = new HashMap<>();
            byTitlelist.forEach(f -> filmsMap.put(f.getId(), f));
            byDirectorList.forEach(f -> filmsMap.put(f.getId(), f));

            result = filmsMap.values().stream().map(FilmMapper::maptoFilmDTO).toList();
        }
        return result;
    }
}
