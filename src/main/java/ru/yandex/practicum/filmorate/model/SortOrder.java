package ru.yandex.practicum.filmorate.model;

public enum SortOrder {
    SORT_BY_YEAR,
    SORT_BY_LIKES,
    SORT_BY_ID;

    public static SortOrder from(String order) {
        return switch (order.toLowerCase()) {
            case "year" -> SORT_BY_YEAR;
            case "likes" -> SORT_BY_LIKES;
            default -> SORT_BY_ID;
        };
    }
}
