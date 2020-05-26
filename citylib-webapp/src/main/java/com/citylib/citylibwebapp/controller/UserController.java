package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.model.LoanBean;
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
        Page<LoanBean> userLoans = servicesProxy.getUserLoans(loggedUser.getId(), request.getParameter("page"));
        for (LoanBean loanBean : userLoans) {
            if (loanBean.getDue().isBefore(LocalDate.now())) {
                loanBean.setExpired(true);
            } else {
                loanBean.setExpired(false);
            }
        }
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("userLoans", userLoans);
        return "my_account";
    }

}
