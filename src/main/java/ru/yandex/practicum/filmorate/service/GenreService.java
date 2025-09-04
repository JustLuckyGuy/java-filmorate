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

    public List<Genre> getAllGenres(){
        return genreRepository.findAllGenre();
    }

    public Genre getGenreById(long genreId){
        return genreRepository.findByIdGenre(genreId).orElseThrow(()->new NotFoundException("Данный жанр не найден"));
    }

    public Genre createGenre(Genre genre){
        return genreRepository.save(genre);
    }

    public Genre update(long genreId, Genre newName){
        Genre genre = genreRepository.findByIdGenre(genreId).orElseThrow(() -> new NotFoundException("Жанр не найден"));
        genre.setName(newName.getName());
        return genreRepository.update(genre);
    }

    public boolean delete(long genreId){
        return genreRepository.delete(genreId);
    }

    public boolean deleteR(long filmId){
        return genreRepository.deleteRelationship(filmId);
    }
}
