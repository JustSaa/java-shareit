package ru.practicum.shareit.exception;

public class EmailNotUniqueException extends RuntimeException {
    public EmailNotUniqueException() {
        super();
    }

    public EmailNotUniqueException(String message) {
        super(message);
    }
}
