package com.citylib.citylibwebapp.service;

import com.citylib.citylibwebapp.dto.UserDto;
import com.citylib.citylibwebapp.model.RoleBean;
import com.citylib.citylibwebapp.model.UserBean;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        UserBean user = servicesProxy.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Email ou mot de passe invalide.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
            user.getPassword(),
            mapRolesToAuthorities(user.getRoles()));
    }

    public UserBean findByEmail(String email) {
        return servicesProxy.getUserByEmail(email);
    }

    public UserBean save(UserDto registration) {
        UserBean user = new UserBean();
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setUsername(registration.getUsername());
        return servicesProxy.registerUserAccount(user);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<RoleBean> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }



}
