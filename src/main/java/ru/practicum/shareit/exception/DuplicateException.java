package ru.practicum.shareit.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
