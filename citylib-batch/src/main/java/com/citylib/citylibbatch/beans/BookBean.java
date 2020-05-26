package com.citylib.citylibbatch.beans;

import lombok.*;

/**
 * Object storing all the book related informations served by the citylib-services service.
 *
 * @author crosart
 */
@Data
public class BookBean {

    private long id;
    private String isbn;
    private String title;
    private String author;
    private String editor;
    private int year;
    private String summary;
    private int quantity;
    private String image;
    private String genre;
    private int available;

}
