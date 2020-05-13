package com.citylib.citylibwebapp.service;

import com.citylib.citylibwebapp.dto.UserDto;
import com.citylib.citylibwebapp.model.Role;
import com.citylib.citylibwebapp.model.User;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private CitylibServicesProxy servicesProxy;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = servicesProxy.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Email ou mot de passe invalide.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
            user.getPassword(),
            mapRolesToAuthorities(user.getRoles()));
    }

    public User findByEmail(String email) {
        return servicesProxy.getUserByEmail(email);
    }

    public User save(UserDto registration) {
        User user = new User();
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setUsername(registration.getUsername());
        return servicesProxy.registerUserAccount(user);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }



}
