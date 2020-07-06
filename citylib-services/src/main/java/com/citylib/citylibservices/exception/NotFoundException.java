package com.citylib.citylibservices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception if no more reservations can be registered for a book.
 *
 * Returns HTTP code 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }

}
