package com.citylib.citylibservices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            schema = "citylib_db",
            name = "users_roles",
            joinColumns = {@JoinColumn(
                    name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(
                    name = "role_id", referencedColumnName = "id")}
                    )
    private Collection<Role> roles;

}
