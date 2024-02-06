package ru.practicum.shareit.exception;

public class UnavailableItemException extends RuntimeException {
    public UnavailableItemException() {
        super();
    }

    public UnavailableItemException(Integer id) {
        super("Item " + id + " is unavailable now");
    }
}
