package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FeedBlock {
    Long eventId;
    Long userId;
    String eventType;
    String operation;
    Long entityId;
    Long timestamp;
}
