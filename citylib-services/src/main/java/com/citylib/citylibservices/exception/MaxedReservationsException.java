package com.citylib.citylibservices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception if no more reservations can be registered for a book.
 *
 * Returns HTTP code 412.
 */
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class MaxedReservationsException extends Exception {

    public MaxedReservationsException(String message) {
        super(message);
    }

}
