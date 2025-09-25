package ru.yandex.practicum.filmorate.model.enums;


public enum EventType {
    LIKE,
    REVIEW,
    FRIEND,
    EMPTY;

    public static EventType from(String order) {
        return switch (order.toLowerCase()) {
            case "like" -> LIKE;
            case "review" -> REVIEW;
            case "friend" -> FRIEND;
            default -> EMPTY;
        };
    }
}
