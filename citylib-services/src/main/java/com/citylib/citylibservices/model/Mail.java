package com.citylib.citylibservices.model;

import lombok.Data;

@Data
public class Mail {

    private String subject;
    private String body;
    private String recipient;

}
