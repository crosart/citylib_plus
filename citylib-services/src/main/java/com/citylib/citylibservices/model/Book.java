package com.citylib.citylibservices.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Book entity linked to the database.
 *
 * @author crosart
 */
@Entity
@Data
@Table(schema = "citylib_db", name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Transient
    private int available;

}
