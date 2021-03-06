package com.citylib.citylibbatch.beans;

import lombok.*;

/**
 * Object storing all the user related informations served by the citylib-services service.
 *
 * @author crosart
 */
@Data
public class UserBean {

    private long id;
    private String email;
    private String password;
    private String username;

}
