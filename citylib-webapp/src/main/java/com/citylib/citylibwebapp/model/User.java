package com.citylib.citylibwebapp.bean;

import lombok.Data;

@Data
public class User {

    private long id;
    private String email;
    private String password;
    private String username;
    private boolean enabled;

}
