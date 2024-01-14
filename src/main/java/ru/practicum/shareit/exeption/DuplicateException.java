package ru.practicum.shareit.exeption;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
