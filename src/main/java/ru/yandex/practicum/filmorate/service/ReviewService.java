package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class  ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmDb;
    private final UserStorage userStorage;

    public ReviewResponse add(ReviewRequest reviewRequest) {
        validateUserAndFilm(reviewRequest.getUserId(), reviewRequest.getFilmId());

        Review review = ReviewMapper.convertToReview(reviewRequest);
        review.setUseful(0);

        Review createdReview = reviewStorage.add(review);
        return ReviewMapper.convertToResponse(createdReview);
    }

    public ReviewResponse update(ReviewRequest reviewRequest) {
        if (reviewRequest.getReviewId() == null) {
            throw new ValidationException("ID отзыва обязателен для обновления");
        }

        Review existingReview = reviewStorage.getById(reviewRequest.getReviewId());
        validateUserAndFilm(reviewRequest.getUserId(), reviewRequest.getFilmId());

        ReviewMapper.updateReviewFromRequest(existingReview, reviewRequest);
        Review updatedReview = reviewStorage.update(existingReview);

        return ReviewMapper.convertToResponse(updatedReview);
    }

    public void delete(Long id) {
        reviewStorage.delete(id);
    }

    public ReviewResponse getById(Long id) {
        Review review = reviewStorage.getById(id);
        return ReviewMapper.convertToResponse(review);
    }

    public List<ReviewResponse> getReviews(Long filmId, int count) {
        List<Review> reviews;
        if (filmId != null) {
            filmDb.findById(filmId);
            reviews = reviewStorage.getByFilmId(filmId, count);
        } else {
            reviews = reviewStorage.getAll(count);
        }

        return reviews.stream()
                .map(ReviewMapper::convertToResponse)
                .toList();
    }

    public void like(Long reviewId, Long userId) {
        processReaction(reviewId, userId, true);
    }

    public void dislike(Long reviewId, Long userId) {
        processReaction(reviewId, userId, false);
    }

    public void removeLike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, true);
    }

    public void removeDislike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, false);
    }

    private void processReaction(Long reviewId, Long userId, boolean isLike) {
        Review review = reviewStorage.getById(reviewId);
        userStorage.findById(userId);

        Set<Long> targetReactions = isLike ? review.getLikes() : review.getDislikes();
        Set<Long> oppositeReactions = isLike ? review.getDislikes() : review.getLikes();
        String errorMessage = isLike ? "Пользователь уже поставил лайк" : "Пользователь уже поставил дизлайк";
        int usefulChange = isLike ? 1 : -1;

        if (targetReactions.contains(userId)) {
            throw new ValidationException(errorMessage);
        }

        if (oppositeReactions.contains(userId)) {
            oppositeReactions.remove(userId);
            review.setUseful(review.getUseful() + usefulChange);
        }

        targetReactions.add(userId);
        review.setUseful(review.getUseful() + usefulChange);
        reviewStorage.updateLikesDislikes(review);
    }

    private void removeReaction(Long reviewId, Long userId, boolean isLike) {
        Review review = reviewStorage.getById(reviewId);
        userStorage.findById(userId);

        Set<Long> targetReactions = isLike ? review.getLikes() : review.getDislikes();
        String errorMessage = isLike ? "Лайк не найден" : "Дизлайк не найден";
        int usefulChange = isLike ? -1 : 1;

        if (!targetReactions.contains(userId)) {
            throw new NotFoundException(errorMessage);
        }

        targetReactions.remove(userId);
        review.setUseful(review.getUseful() + usefulChange);
        reviewStorage.updateLikesDislikes(review);
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с таким id, отзыв не добавлен");
        }
        if (filmDb.findById(filmId).isEmpty()) {
            throw new NotFoundException("Не найден фильм с таким id, отзыв не добавлен");
        }
    }
}
