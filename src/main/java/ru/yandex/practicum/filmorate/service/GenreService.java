package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dao.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        log.trace("Производится выгрузка всех жанров");
        return genreRepository.findAllGenre();
    }

    public Genre getGenreById(long genreId) {
        log.trace("Производится выгрузка жанра с ID: {}", genreId);
        return genreRepository.findByIdGenre(genreId).orElseThrow(() -> new NotFoundException("Данный жанр не найден"));
    }

    public Genre createGenre(Genre genre) {
        log.trace("В базу данных добавлен новый жанр: {}", genre.getName());
        return genreRepository.save(genre);
    }

    public Genre update(long genreId, Genre newName) {
        Genre genre = genreRepository.findByIdGenre(genreId).orElseThrow(() -> new NotFoundException("Жанр не найден"));
        genre.setName(newName.getName());
        log.trace("Жанр с ID: {} был обновлен", genreId);
        return genreRepository.update(genre);
    }

    public boolean delete(long genreId) {
        log.trace("Жанра с ID: {} удален из базы данных", genreId);
        return genreRepository.delete(genreId);
    }

    public boolean deleteRelations(long filmId) {
        return genreRepository.deleteRelationship(filmId);
    }
}
