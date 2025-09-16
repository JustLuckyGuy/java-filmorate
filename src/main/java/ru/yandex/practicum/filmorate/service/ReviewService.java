package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class  ReviewService {
    private final ReviewMapper reviewMapper;
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmDb;
    private final UserStorage userStorage;

    public ReviewResponse add(ReviewRequest reviewRequest) {
        validateUserAndFilm(reviewRequest.getUserId(), reviewRequest.getFilmId());

        Review review = reviewMapper.convertToReview(reviewRequest);
        review.setUseful(0);

        Review createdReview = reviewStorage.add(review);
        return reviewMapper.convertToResponse(createdReview);
    }

    public ReviewResponse update(ReviewRequest reviewRequest) {
        if (reviewRequest.getReviewId() == null) {
            throw new ValidationException("ID отзыва обязателен для обновления");
        }

        Review existingReview = reviewStorage.getById(reviewRequest.getReviewId());
        validateUserAndFilm(reviewRequest.getUserId(), reviewRequest.getFilmId());

        reviewMapper.updateReviewFromRequest(existingReview, reviewRequest);
        Review updatedReview = reviewStorage.update(existingReview);

        return reviewMapper.convertToResponse(updatedReview);
    }

    public void delete(int id) {
        reviewStorage.delete(id);
    }

    public ReviewResponse getById(int id) {
        Review review = reviewStorage.getById(id);
        return reviewMapper.convertToResponse(review);
    }

    public List<ReviewResponse> getReviews(Integer filmId, int count) {
        List<Review> reviews;
        if (filmId != null) {
            filmDb.findById(filmId);
            reviews = reviewStorage.getByFilmId(filmId, count);
        } else {
            reviews = reviewStorage.getAll(count);
        }

        return reviews.stream()
                .map(reviewMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    public void like(int reviewId, Long userId) {
        Review review = reviewStorage.getById(reviewId);
        userStorage.findById(userId);

        if (review.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк");
        }

        if (review.getDislikes().contains(userId)) {
            review.getDislikes().remove(userId);
            review.setUseful(review.getUseful() + 1);
        }

        review.getLikes().add(userId);
        review.setUseful(review.getUseful() + 1);
        reviewStorage.update(review);
    }

    public void dislike(int reviewId, Long userId) {
        Review review = reviewStorage.getById(reviewId);
        userStorage.findById(userId);

        if (review.getDislikes().contains(userId)) {
            throw new ValidationException("Пользователь уже поставил дизлайк");
        }

        if (review.getLikes().contains(userId)) {
            review.getLikes().remove(userId);
            review.setUseful(review.getUseful() - 1);
        }

        review.getDislikes().add(userId);
        review.setUseful(review.getUseful() - 1);
        reviewStorage.update(review);
    }

    public void removeLike(int reviewId, Long userId) {
        Review review = reviewStorage.getById(reviewId);
        userStorage.findById(userId);

        if (!review.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк не найден");
        }

        review.getLikes().remove(userId);
        review.setUseful(review.getUseful() - 1);
        reviewStorage.update(review);
    }

    public void removeDislike(int reviewId, Long userId) {
        Review review = reviewStorage.getById(reviewId);
        userStorage.findById(userId);

        if (!review.getDislikes().contains(userId)) {
            throw new NotFoundException("Дизлайк не найден");
        }

        review.getDislikes().remove(userId);
        review.setUseful(review.getUseful() + 1);
        reviewStorage.update(review);
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с таким id");
        };
        if (filmDb.findById(filmId).isEmpty()) {
            throw new NotFoundException("Не найден фильм с таким id");
        };
    }
}
