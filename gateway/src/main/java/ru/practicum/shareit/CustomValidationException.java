package ru.practicum.shareit;

public class CustomValidationException extends RuntimeException {
    public CustomValidationException() {
        super();
    }

    public CustomValidationException(String message) {
        super(message);
    }
}