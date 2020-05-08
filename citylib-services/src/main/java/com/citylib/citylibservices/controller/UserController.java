package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.model.User;
import com.citylib.citylibservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/users/{id}")
    public Optional<User> getUserById(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);

        return user;
    }

    @GetMapping(value = "/users/email/{email}")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);

        return user;
    }
}
