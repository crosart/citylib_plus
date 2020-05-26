package com.citylib.citylibwebapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Object storing all the role related informations served by the citylib-services service.
 *
 * @author crosart
 */
@Data
@AllArgsConstructor
public class RoleBean {

    private long id;
    private String name;

}

