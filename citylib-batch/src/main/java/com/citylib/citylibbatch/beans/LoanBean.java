package com.citylib.citylibbatch.beans;

import lombok.*;

import java.time.LocalDate;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanBean {

    private long id;
    private long bookId;
    private long userId;
    private LocalDate due;
    private boolean returned;
    private boolean extended;

}
