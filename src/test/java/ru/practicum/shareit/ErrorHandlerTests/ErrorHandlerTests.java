package ru.practicum.shareit.ErrorHandlerTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTests {

    @Mock
    private ErrorResponse errorResponseMock;

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void testNonFoundHandler() {
        NotFoundException notFoundException = new NotFoundException("Not Found");

        ErrorResponse errorResponse = errorHandler.nonFoundHandler(notFoundException);

        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
    }

    @Test
    public void testValidationErrorHandler() {
        ValidationException validationException = new ValidationException("Bad Request");

        ErrorResponse errorResponse = errorHandler.validationErrorHandler(validationException);

        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
    }

    @Test
    public void testInternalServerErrorHandler() {
        RuntimeException runtimeException = new RuntimeException("Internal Server Error");

        ErrorResponse errorResponse = errorHandler.internalServerErrorHandler(runtimeException);

        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
    }

    @Test
    public void testUnauthorizedUser() {
        UnauthorizedException unauthorizedException = new UnauthorizedException("Unauthorized");

        ErrorResponse errorResponse = errorHandler.unauthorizedUser(unauthorizedException);

        assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getStatus());
    }
}
