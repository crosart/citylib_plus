package com.citylib.citylibbatch.beans;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserBean {

    private long id;
    private String email;
    private String password;
    private String username;

}
