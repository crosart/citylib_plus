package com.citylib.citylibwebapp.dto;

import com.citylib.citylibwebapp.constraint.FieldMatch;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO user object to pass user data to the backend service.
 *
 * @author crosart
 */
@Data
@FieldMatch.List({
        @FieldMatch(first = "password", second = "matchingPassword", message = "La confirmation du mot de passe diffère du mot de passe."),
        })
public class UserDto {
    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    private String username;
}
