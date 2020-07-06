package com.citylib.citylibwebapp.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Object storing all the book related informations served by the citylib-services service.
 *
 * @author crosart
 */
@Data
public class ReservationBean {
    private long id;
    private LocalDate notificationDate;
    private BookBean book;
    private UserBean user;
    private long position;
    private LocalDate returnDate;
}
