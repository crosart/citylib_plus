package com.citylib.citylibwebapp.proxy;

import com.citylib.citylibwebapp.dto.UserDto;
import com.citylib.citylibwebapp.model.Book;
import com.citylib.citylibwebapp.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "citylib-services")
public interface CitylibServicesProxy {

    @GetMapping(value = "/books")
    List<Book> getAllBooks();

    @GetMapping(value = "/books/{id}")
    Book getBookById(@PathVariable("id") long id);

    @RequestMapping("/users/email/{email}/")
    User getUserByEmail(@PathVariable("email") String email);

    @PostMapping("users/register")
    User registerUserAccount(User user);

}
