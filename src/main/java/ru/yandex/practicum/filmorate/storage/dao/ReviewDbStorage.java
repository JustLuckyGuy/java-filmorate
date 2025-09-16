package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.row_mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;
    private final FilmStorage filmDb;
    private final UserStorage userStorage;

    private static final String GET_REVIEW_BY_ID_SQL = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String GET_ALL_REVIEWS_SQL = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String GET_REVIEWS_BY_FILM_SQL = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String DELETE_REVIEW_SQL = "DELETE FROM reviews WHERE review_id = ?";
    private static final String UPDATE_REVIEW_SQL = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";

    @Override
    public Review add(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        parameters.put("useful", review.getUseful());
        parameters.put("created_at", review.getCreatedAt());

        Number generatedId = simpleJdbcInsert.executeAndReturnKey(parameters);
        review.setReviewId(generatedId.intValue());

        getLikesDislikes(review);
        return review;
    }

    @Override
    public Review update(Review review) {
        int rowsUpdated = jdbcTemplate.update(UPDATE_REVIEW_SQL,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Отзыв с ID " + review.getReviewId() + " не найден");
        }

        updateLikesDislikes(review);
        Review updatedReview = getById(review.getReviewId());
        getLikesDislikes(updatedReview);

        return updatedReview;
    }

    @Override
    public Review getById(int id) {
        Review review = jdbcTemplate.query(GET_REVIEW_BY_ID_SQL, reviewRowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + id + " не найден"));

        getLikesDislikes(review);
        return review;
    }

    @Override
    public List<Review> getAll(int count) {
        List<Review> reviews = jdbcTemplate.query(GET_ALL_REVIEWS_SQL, reviewRowMapper, count);
        reviews.forEach(this::getLikesDislikes);
        return reviews;
    }

    @Override
    public List<Review> getByFilmId(int filmId, int count) {
        List<Review> reviews = jdbcTemplate.query(GET_REVIEWS_BY_FILM_SQL, reviewRowMapper, filmId, count);
        reviews.forEach(this::getLikesDislikes);
        return reviews;
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_REVIEW_SQL, id);
    }

    @Override
    public void getLikesDislikes(Review review) {
        String likesSql = "SELECT user_id FROM review_likes WHERE review_id = ? AND is_like = true";
        review.setLikes(new java.util.HashSet<>(jdbcTemplate.query(likesSql,
                (resultSet, rowNum) -> resultSet.getLong("user_id"),
                review.getReviewId())));

        String dislikesSql = "SELECT user_id FROM review_likes WHERE review_id = ? AND is_like = false";
        review.setDislikes(new java.util.HashSet<>(jdbcTemplate.query(dislikesSql,
                (resultSet, rowNum) -> resultSet.getLong("user_id"),
                review.getReviewId())));
    }

    private void updateLikesDislikes(Review review) {
        String deleteSql = "DELETE FROM review_likes WHERE review_id = ?";
        jdbcTemplate.update(deleteSql, review.getReviewId());

        if (!review.getLikes().isEmpty()) {
            String insertLikesSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
            review.getLikes().forEach(userId ->
                    jdbcTemplate.update(insertLikesSql, review.getReviewId(), userId)
            );
        }

        if (!review.getDislikes().isEmpty()) {
            String insertDislikesSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
            review.getDislikes().forEach(userId ->
                    jdbcTemplate.update(insertDislikesSql, review.getReviewId(), userId)
            );
        }
    }
}