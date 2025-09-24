package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {
    private static final String GET_REVIEW_BY_ID_SQL = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String GET_ALL_REVIEWS_SQL = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String GET_REVIEWS_BY_FILM_SQL = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String DELETE_REVIEW_SQL = "DELETE FROM reviews WHERE review_id = ?";
    private static final String UPDATE_REVIEW_SQL = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";
    private static final String INSERT_REVIEW = "INSERT INTO reviews(content, is_positive, user_id, film_id, useful,created_at) " +
            "VALUES (?,?,?,?,?,?)";
    private final FilmStorage filmDb;
    private final UserStorage userStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Review> mapper, FilmStorage filmDb, UserStorage userStorage) {
        super(jdbcTemplate, mapper);
        this.filmDb = filmDb;
        this.userStorage = userStorage;
    }

    @Override
    public Review add(Review review) {
        long id = insert(INSERT_REVIEW,
                "review_id", review.getContent(), review.getIsPositive(), review.getUserId(),
                review.getFilmId(), review.getUseful(), review.getCreatedAt());
        review.setReviewId(id);
        getLikesDislikes(review);
        update(INSERT_FEED, review.getUserId(), "REVIEW", "ADD", review.getReviewId(), Timestamp.from(Instant.now()));
        return review;
    }

    @Override
    public Review update(Review review) {
        update(UPDATE_REVIEW_SQL,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId()
        );
        updateLikesDislikes(review);
        Review updatedReview = getById(review.getReviewId());
        getLikesDislikes(updatedReview);
        update(INSERT_FEED, review.getUserId(), "REVIEW", "UPDATE", review.getReviewId(), Timestamp.from(Instant.now()));
        return updatedReview;
    }

    @Override
    public Review getById(Long id) {
        Optional<Review> review = findOne(GET_REVIEW_BY_ID_SQL, id);
        if (review.isEmpty())
            throw new NotFoundException("Такого ревью не существует");
        review.ifPresent(this::getLikesDislikes);
        return review.orElse(null);
    }

    @Override
    public List<Review> getAll(int count) {
        List<Review> reviews = findMany(GET_ALL_REVIEWS_SQL, count);
        reviews.forEach(this::getLikesDislikes);
        return reviews;
    }

    @Override
    public List<Review> getByFilmId(Long filmId, int count) {
        List<Review> reviews = findMany(GET_REVIEWS_BY_FILM_SQL, filmId, count);
        reviews.forEach(this::getLikesDislikes);
        return reviews;
    }

    @Override
    public void delete(Long id) {
        Review review = getById(id);
        delete(DELETE_REVIEW_SQL, id);
        update(INSERT_FEED, review.getUserId(), "REVIEW", "REMOVE", review.getReviewId(), Timestamp.from(Instant.now()));
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

    public void updateLikesDislikes(Review review) {
        String deleteSql = "DELETE FROM review_likes WHERE review_id = ?";
        delete(deleteSql, review.getReviewId());

        if (!review.getLikes().isEmpty()) {
            String insertLikesSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
            review.getLikes().forEach(userId ->
                    update(insertLikesSql, review.getReviewId(), userId)
            );
        }

        if (!review.getDislikes().isEmpty()) {
            String insertDislikesSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
            review.getDislikes().forEach(userId ->
                    update(insertDislikesSql, review.getReviewId(), userId)
            );
        }

        update("UPDATE reviews SET useful = ? WHERE review_id = ?", review.getUseful(), review.getReviewId());
    }
}