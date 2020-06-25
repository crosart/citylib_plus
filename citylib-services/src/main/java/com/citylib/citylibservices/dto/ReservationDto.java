package com.citylib.citylibservices.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO reservation object to retrieve data sent by the frontend service and serve it to the controller.
 *
 * @author crosart
 */
@Data
public class ReservationDto {

    @NotNull
    @NotEmpty
    private long id;

    @NotNull
    @NotEmpty
    private long bookId;

    @NotNull
    @NotEmpty
    private long userId;

}
