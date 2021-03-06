package com.citylib.citylibservices.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Reservation entity linked to the database.
 *
 * @author crosart
 */
@Entity
@Data
@Table(schema = "citylib_db", name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "notification")
    private LocalDate notificationDate;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
