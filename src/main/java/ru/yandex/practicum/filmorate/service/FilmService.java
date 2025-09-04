package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;


@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmDb;
    private final UserStorage inMemoryUserStorage;
    private final Comparator<Film> comparator = Comparator.comparingLong((Film film) -> film.getLikes().size()).reversed();

    @Autowired
    public FilmService(@Qualifier("FilmDb") FilmStorage filmDb, @Qualifier("UserDb") UserStorage inMemoryUserStorage) {
        this.filmDb = filmDb;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmDb.allFilms();
    }

    public FilmDTO findFilmById(long filmId){
        return filmDb.findById(filmId).map(FilmMapper::maptoFilmDTO).orElseThrow(() -> new NotFoundException("Не удалось найти фильм"));
    }


    public FilmDTO createFilm(NewFilmRequest film) {
        Film createdFilm = FilmMapper.mapToFilm(film);
        createdFilm = filmDb.create(createdFilm);
        return FilmMapper.maptoFilmDTO(createdFilm);
    }

    public FilmDTO updateFilm(long filmId, UpdateFilmRequest film) {
        Film film1 = filmDb.findById(filmId).map(film2 -> FilmMapper.updateFieldsFilms(film2, film))
                .orElseThrow(()-> new NotFoundException("Фильм не найден"));
        film1 = filmDb.update(film1);
        return FilmMapper.maptoFilmDTO(film1);
    }

    public boolean deleteFilm(long id){
        return filmDb.delete(id);
    }

//    public Film setLike(Long idFilm, Long idUser) {
//        Film film = searchFilm(idFilm, idUser);
//        if (film.getLikes().contains(idUser)) {
//            throw new ValidationException("Пользователь с id = " + idUser + "уже поставил лайк");
//        }
//        film.getLikes().add(idUser);
//        log.trace("Пользователь с id={} поставил лайк фильму {}", idUser, film.getName());
//        return film;
//    }

//    public Film removeLike(Long idFilm, Long idUser) {
//        Film film = searchFilm(idFilm, idUser);
//        film.getLikes().remove(idUser);
//        log.trace("Пользователь с id={} убрал лайк фильму {}", idUser, film.getName());
//        return film;
//    }
//
    public List<Film> getPopularFilm(Integer count) {
        log.trace("Был произведен вывод популярных фильмов");
        return filmDb.allFilms().stream()
                .sorted(comparator)
                .limit(count)
                .toList();
    }

//    private Film searchFilm(Long idFilm, Long idUser) {
//        User user = inMemoryUserStorage.findById(idUser).orElseThrow(() -> new NotFoundException("Пользователь c id = " + idUser + " не найден"));
//
//        return inMemoryFilmStorage.findById(idFilm).orElseThrow(() ->
//                new NotFoundException("Не удалось найти фильм"));
//    }

}
