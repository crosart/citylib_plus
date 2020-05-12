package com.citylib.citylibwebapp.proxies;

import com.citylib.citylibwebapp.beans.Book;
import com.citylib.citylibwebapp.beans.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "citylib-services")
public interface CitylibServicesProxy {

    @GetMapping(value = "/books")
    List<Book> getAllBooks();

    @GetMapping(value = "/books/{id}")
    Book getBookById(@PathVariable("id") long id);

    @GetMapping("/users/email/{email}")
    User getUserByEmail(@PathVariable("email") String email);

}
