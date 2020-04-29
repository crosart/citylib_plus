package com.citylib.citylibservices.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(schema = "citylib_db", name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;
    private String password;
    private String username;

}
