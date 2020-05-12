package com.citylib.citylibwebapp.service;

import com.citylib.citylibwebapp.dto.UserDto;
import com.citylib.citylibwebapp.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);
    User save(UserDto registration);

}
