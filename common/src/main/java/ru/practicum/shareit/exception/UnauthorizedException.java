package ru.practicum.shareit.exception;

public class UnauthorizedException extends RuntimeException {
    private final String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
