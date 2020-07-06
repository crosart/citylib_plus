package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.model.ReservationBean;
import com.citylib.citylibwebapp.model.UserBean;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

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

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable("id") long id, Principal principal) throws Exception {
        UserBean loggedUser = servicesProxy.getUserByEmail(principal.getName());
        Optional<ReservationBean> reservation = servicesProxy.getReservationById(id);
        if (reservation.isPresent()) {
            if (reservation.get().getUser().getId() == loggedUser.getId()) {
                servicesProxy.deleteReservation(id);
            } else {
                throw new Exception("Opération non autorisée !");
            }
        } else {
            throw new Exception("Réservation non trouvée. ID=" + id);
        }
        return "redirect:/users/account/?page=1";
    }

}
