package ru.practicum.shareit.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<List<String>> nonFoundHandler(final NotFoundException e) {
        return new ResponseEntity<>(List.of(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> validationErrorHandler(final ValidationException e) {
        return new ResponseEntity<>(List.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> internalServerErrorHandler(final RuntimeException e) {
        return new ResponseEntity<>(List.of("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> duplicateErrorHandler(final DuplicateException e) {
        return new ResponseEntity<>(List.of(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> unauthorizedUser(final UnauthorizedException e) {
        return new ResponseEntity<>(List.of(e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
