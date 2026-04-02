package com.hospital.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * Exception thrown when a requested resource is not found.
 *
 * This exception is used to signal that an attempt to retrieve an entity
 * from a database or another data source has failed because the entity
 * does not exist. It is annotated with @ResponseStatus to automatically
 * return a 404 Not Found HTTP status to the client.
 *
 * Constructors:
 * - NotFoundException(): Constructs a NotFoundException with a default message.
 * - NotFoundException(String message): Constructs a NotFoundException with a specified error message.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException() {
        super("Not found!");
    }

    public NotFoundException(final String message) {
        super(message);
    }
}