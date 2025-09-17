package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review);

    Review getById(Long id);

    List<Review> getAll(int count);

    List<Review> getByFilmId(Long filmId, int count);

    void delete(Long id);

    void getLikesDislikes(Review review);
}