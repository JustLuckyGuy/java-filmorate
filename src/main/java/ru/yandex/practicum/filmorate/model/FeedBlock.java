package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
public class FeedBlock {
    Long eventId;
    Long userId;
    EventType eventType;
    Operation operation;
    Long entityId;
    Long timestamp;
}
