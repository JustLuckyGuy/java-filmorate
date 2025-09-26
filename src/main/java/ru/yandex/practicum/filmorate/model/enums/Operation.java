package ru.yandex.practicum.filmorate.model.enums;

public enum Operation {
    ADD,
    REMOVE,
    UPDATE,
    EMPTY;

    public static Operation from(String order) {
        return switch (order.toLowerCase()) {
            case "add" -> ADD;
            case "remove" -> REMOVE;
            case "update" -> UPDATE;
            default -> EMPTY;
        };
    }

}
