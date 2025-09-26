package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Review {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private Integer useful;
    private LocalDateTime createdAt;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @Builder.Default
    private Set<Long> dislikes = new HashSet<>();
}
