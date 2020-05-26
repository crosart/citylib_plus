package com.citylib.citylibwebapp.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Object storing all the loan related informations served by the citylib-services service.
 *
 * @author crosart
 */
@Data
public class LoanBean {

    private long id;
    private LocalDate due;
    private boolean returned;
    private boolean extended;
    private BookBean book;
    private UserBean user;
    private boolean expired;

}