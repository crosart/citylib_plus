package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.dto.UserDto;
import com.citylib.citylibwebapp.model.UserBean;
import com.citylib.citylibwebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * Service controller for registration related operations.
 *
 * @author crosart
 */
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    UserService userService;

    /**
     * Prepares a userDto model to present to the view.
     *
     * @return {@link UserDto}
     */
    @ModelAttribute("user")
    public UserDto userRegistrationDto() {
        return new UserDto();
    }

    /**
     * Show the registration page when "GET" requested.
     *
     * @param model The retrieved parameters of the page.
     * @return The template name.
     */
    @GetMapping
    public String showRegistrationForm(Model model) {
        return "registration";
    }

    /**
     * Registers the inputted new user.
     *
     * @param userDto The object containing the required parameters (email, password, password confirmation, username)
     * @param result The result parameter to pass to the page.
     * @return The template name (with success parameter if OK).
     */
    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto, BindingResult result) {
        UserBean existing = userService.findByEmail(userDto.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "Un compte existe déjà pour cette adresse mail");
        }

        if (result.hasErrors()) {
            return "registration";
        }

        userService.save(userDto);
        return "redirect:/registration?success";
    }
}
