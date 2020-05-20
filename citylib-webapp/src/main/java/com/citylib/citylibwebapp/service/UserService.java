package com.citylib.citylibwebapp.service;

import com.citylib.citylibwebapp.dto.UserDto;
import com.citylib.citylibwebapp.model.UserBean;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserBean findByEmail(String email);
    UserBean save(UserDto registration);

}
