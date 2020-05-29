package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Service Controller for loan related operations.
 *
 * @author crosart
 */
@Controller
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    CitylibServicesProxy servicesProxy;

    /**
     * Extends the selected loan due date.
     *
     * @param id ID of the loan.
     * @return The template name.
     */
    @GetMapping("/extend/{id}")
    public String extendLoan(@PathVariable("id") long id) {
        servicesProxy.extendLoan(id);
        return "redirect:/users/account/?page=1";
    }

}
