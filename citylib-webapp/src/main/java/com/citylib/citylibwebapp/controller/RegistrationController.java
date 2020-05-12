package com.citylib.citylibwebapp.controllers;

import com.citylib.citylibwebapp.beans.User;
import com.citylib.citylibwebapp.dto.UserDto;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public class RegistrationController {

    @GetMapping("/register")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping("/register")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto,
                                            HttpServletRequest request, Errors errors) {
        try {
            User registered = userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistsException uaeEx) {
            mav.addObject
        }
    }
}
