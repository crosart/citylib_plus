package com.citylib.citylibwebapp.model;

import lombok.*;

@Data
public class Book {

    private long id;
    private String isbn;
    private String title;
    private String author;
    private String editor;
    private int year;
    private String summary;
    private int quantity;
    private String image;
    private int available;

}
