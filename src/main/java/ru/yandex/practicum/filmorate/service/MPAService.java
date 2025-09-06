package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dao.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MPAService {
    private final MpaRepository mpaRepository;

    public List<MPA> getAllMPA() {
        log.trace("Производится выгрузка всех рейтингов");
        return mpaRepository.findAllMPA();
    }

    public MPA getMpaById(long mpaId) {
        log.trace("Производится выгрузка рейтинга с ID: {}", mpaId);
        return mpaRepository.findByIdMPA(mpaId).orElseThrow(() -> new NotFoundException("Данный рейтинг не найден"));
    }

    public MPA createMpa(MPA mpa) {
        log.info("Новый рейтинг добавлен в базу данных: {}", mpa.getName());
        return mpaRepository.save(mpa);
    }

    public MPA update(long mpaId, MPA newRating) {

        MPA mpa = mpaRepository.findByIdMPA(mpaId).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
        mpa.setName(newRating.getName());
        log.info("Рейтинг с ID: {} был обновлен", mpaId);
        return mpaRepository.update(mpa);
    }

    public boolean delete(long mpaId) {
        log.trace("Рейтинг с ID: {} был удален из базы данных", mpaId);
        return mpaRepository.delete(mpaId);
    }
}
