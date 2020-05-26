package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.exception.UserAlreadyExistsException;
import com.citylib.citylibservices.model.Role;
import com.citylib.citylibservices.model.User;
import com.citylib.citylibservices.repository.RoleRepository;
import com.citylib.citylibservices.repository.UserRepository;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller centralizing all the user related operations.
 *
 * @author crosart
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    /**
     * Retrieves the user corresponding to the served id.
     *
     * @param id ID of the user.
     * @return The retrieved user (or empty object if not found).
     */
    @GetMapping("/id/{id}")
    public Optional<User> getUserById(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);
        return user;
    }

    /**
     * Retrieves the user corresponding to the served email.
     *
     * @param email Email of the user.
     * @return The retrieved user (or empty object if not found).
     */
    @GetMapping("/email/{email}/")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(StringEscapeUtils.unescapeHtml(email));
        if (user.isPresent()) {
            Collection<Role> defaultRoles = new ArrayList<>();
            defaultRoles.add(roleRepository.findByDefTrue());
            user.get().setRoles(defaultRoles);
        }
        return user;
    }

    /**
     * Registers a new user in the database.
     *
     * @param userDto The object containing the required infos for registering (email, password, username)
     * @return Http Code 201 with the registered infos for confirmation purposes.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User userDto) {
        User user = new User();
        Optional<User> existing = userRepository.findByEmail(userDto.getEmail());
        if (existing.isPresent()) {
            throw new UserAlreadyExistsException("Un compte existe déjà pour l'adresse email : " + userDto.getEmail());
        } else {
            user.setEmail(userDto.getEmail());
            user.setPassword(userDto.getPassword());
            user.setUsername(userDto.getUsername());
            userRepository.save(user);
        }
        return new ResponseEntity<User>(userDto, HttpStatus.CREATED);
    }

}
