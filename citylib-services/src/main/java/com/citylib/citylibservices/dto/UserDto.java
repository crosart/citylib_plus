package com.citylib.citylibservices.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO user object to retrieve data sent by the frontend service and serve it to the controller.
 *
 * @author crosart
 */
@Data
public class UserDto {
    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    private String username;
}
