package com.citylib.citylibservices.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(schema = "citylib_db", name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "book_id")
    private long bookId;
    @Column(name = "user_id")
    private long userId;

    private LocalDate due;
    private boolean returned;
    private boolean extended;

}
