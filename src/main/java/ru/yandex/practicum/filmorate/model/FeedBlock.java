package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class FeedBlock {
    Long eventId;
    Long userId;
    String eventType;
    String operation;
    Long entityId;
    Instant createdAt;
}
