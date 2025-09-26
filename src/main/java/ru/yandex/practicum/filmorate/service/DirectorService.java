package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public List<Director> getAllDirectors() {
        log.trace("Производится выгрузка всех режиссеров");
        return directorRepository.findAllDirectors();
    }

    public Director getDirectorById(long directorId) {
        log.trace("Производится выгрузка жанра с ID: {}", directorId);
        return directorRepository.findByIdDirector(directorId).orElseThrow(() -> new NotFoundException("Данный режиссер не найден"));
    }

    public Director createDirector(Director directorRequest) {
        log.trace("В базу данных добавлен новый режиссер: {}", directorRequest.getName());
        return directorRepository.save(directorRequest);
    }

    public Director update(Director updateDirectorRequest) {
        Director director = directorRepository.findByIdDirector(updateDirectorRequest.getId())
                .orElseThrow(() -> new NotFoundException("Режиссер не найден"));
        director.setName(updateDirectorRequest.getName());
        log.info("Режиссер с ID: {}.{} был обновлен",director.getId(), director.getName());
        return directorRepository.update(director);
    }

    public boolean delete(long directorId) {
        log.trace("Режиссер с ID: {} удален из базы данных", directorId);
        return directorRepository.delete(directorId);
    }

}
