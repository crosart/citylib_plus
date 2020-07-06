package com.citylib.citylibwebapp.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO reservation object to pass user data to the backend service.
 *
 * @author crosart
 */
@Data
public class ReservationDto {

    @NotNull
    @NotEmpty
    private long bookId;

    @NotNull
    @NotEmpty
    private long userId;
}
