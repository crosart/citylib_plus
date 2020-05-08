package com.citylib.citylibbatch.beans;

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
    private int year;
    private String summary;
    private int quantity;
    private String image;
    private int available;

}
