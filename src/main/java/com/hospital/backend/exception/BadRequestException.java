package com.hospital.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * Exception class representing a Bad Request (HTTP 400) error.
 *
 * This exception is used to indicate that the server cannot process the request
 * due to a client error, such as malformed request syntax or invalid request message framing.
 * It is annotated with {@code @ResponseStatus(HttpStatus.BAD_REQUEST)} to automatically
 * set the HTTP status code to 400 when this exception is thrown.
 *
 * Constructors:
 * - BadRequestException(): Creates a new instance with a default message indicating a bad request.
 * - BadRequestException(String message): Creates a new instance with a custom message.
 *
 * Usage context:
 * - Typically used in service methods to indicate that certain preconditions are not met
 *   or inputs are invalid, causing a 400 Bad Request response to be returned to the client.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException() {
        super("Bad request!");
    }

    public BadRequestException(final String message) {
        super(message);
    }
}

