package com.citylib.citylibwebapp.model;

import lombok.Data;

/**
 * Object storing all the book related informations served by the citylib-services service.
 *
 * @author crosart
 */
@Data
public class ReservationBean {
    private long id;
    private BookBean book;
    private UserBean user;
}