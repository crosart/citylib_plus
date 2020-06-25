package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Service Controller for loan related operations.
 *
 * @author crosart
 */
@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    CitylibServicesProxy servicesProxy;



}
