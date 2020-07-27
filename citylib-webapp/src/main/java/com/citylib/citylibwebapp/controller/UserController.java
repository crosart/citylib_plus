package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.model.LoanBean;
import com.citylib.citylibwebapp.model.ReservationBean;
import com.citylib.citylibwebapp.model.UserBean;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service controller for user related operations.
 *
 * @author crosart
 */
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private CitylibServicesProxy servicesProxy;

    /**
     * Retrieves the current logged user informations to show the user'saccount page.
     *
     * @param request Parameters passed through {@link HttpServletRequest}.
     * @param model Parameters to pass to the webpage.
     * @param principal Parameters of the currently logged user.
     * @return The template name.
     */
    @GetMapping("/account")
    public String showAccountPage(HttpServletRequest request, Model model, Principal principal) {
        UserBean loggedUser = servicesProxy.getUserByEmail(principal.getName());
        String page = Optional.ofNullable(request.getParameter("page")).orElse("1");
        List<ReservationBean> userReservations = servicesProxy.getReservationsListByUserId(loggedUser.getId());
        Page<LoanBean> userLoans = servicesProxy.getUserLoans(loggedUser.getId(), page);
        for (ReservationBean reservationBean : userReservations) {
            reservationBean.setPosition(servicesProxy.countForwardReservations(reservationBean.getBook().getId(), reservationBean.getId()) + 1);
            List<LoanBean> bookLoans = servicesProxy.getLoansListByBookId(reservationBean.getBook().getId());
            reservationBean.setReturnDate(bookLoans.get(0).getDue());
        }
        // TODO LocalDate
        for (LoanBean loanBean : userLoans) {
            loanBean.setExpired(loanBean.getDue().isBefore(LocalDate.now()));
        }
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("userLoans", userLoans);
        model.addAttribute("userReservations", userReservations);
        return "my_account";
    }

}