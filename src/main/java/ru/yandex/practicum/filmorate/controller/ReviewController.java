package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponse;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse add(@Validated @RequestBody ReviewRequest reviewRequest) {
        return reviewService.add(reviewRequest);
    }

    @PutMapping
    public ReviewResponse update(@Validated @RequestBody ReviewRequest reviewRequest) {
        return reviewService.update(reviewRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public ReviewResponse getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<ReviewResponse> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.dislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
    }
}