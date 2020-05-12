package com.citylib.citylibwebapp.model;

import lombok.Data;

import java.util.Collection;

@Data
public class User {

    private long id;
    private String email;
    private String password;
    private String username;
    private Collection<Role> roles;

}
