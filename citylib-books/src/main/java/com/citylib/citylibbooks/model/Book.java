package com.citylib.citylibbooks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;


@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "citylib_books", name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private long id;

    @Getter @Setter
    private String isbn;

    @Getter @Setter
    private String title;

    @Getter @Setter
    private String author;

    @Getter @Setter
    private String editor;

    @Getter @Setter
    private String collection;

    @Getter @Setter
    private int year;

    @JsonIgnore
    @Getter @Setter
    private int availability;

}
