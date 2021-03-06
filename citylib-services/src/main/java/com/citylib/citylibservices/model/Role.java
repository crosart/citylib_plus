package com.citylib.citylibservices.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Role entity linked to the database.
 *
 * @author crosart
 */
@Entity
@Data
@Table(schema = "citylib_db", name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private boolean def;

}
