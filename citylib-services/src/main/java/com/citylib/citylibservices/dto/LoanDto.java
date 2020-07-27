package com.citylib.citylibservices.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LoanDto {

    @NotNull
    @NotEmpty
    private long userId;

    @NotNull
    @NotEmpty
    private long bookId;
}
