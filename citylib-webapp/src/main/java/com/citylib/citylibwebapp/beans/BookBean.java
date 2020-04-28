package com.citylib.citylibwebapp.beans;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookBean {

    private long id;
    private String isbn;
    private String title;
    private String author;
    private String editor;
    private String collection;
    private int year;
    private int availability;

}
