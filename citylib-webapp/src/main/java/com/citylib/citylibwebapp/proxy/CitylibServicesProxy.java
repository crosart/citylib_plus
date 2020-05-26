package com.citylib.citylibwebapp.proxy;

import com.citylib.citylibwebapp.model.BookBean;
import com.citylib.citylibwebapp.model.LoanBean;
import com.citylib.citylibwebapp.model.UserBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "citylib-services")
public interface CitylibServicesProxy {

    @GetMapping("/books")
    Page<BookBean> getLastBooks();

    @GetMapping("/books/all")
    Page<BookBean> getAllBooks(@RequestParam("page") String page);

    @GetMapping("books/id/{id}")
    BookBean getBookById(@PathVariable("id") long id);

    @GetMapping("books/search")
    Page<BookBean> getBooksByQuery(@RequestParam("query") String query, @RequestParam("page") String page);

    @RequestMapping("/users/email/{email}/")
    UserBean getUserByEmail(@PathVariable("email") String email);

    @PostMapping("users/register")
    UserBean registerUserAccount(UserBean user);

    @GetMapping("/loans/user/{id}")
    Page<LoanBean> getUserLoans(@PathVariable("id") long id, @RequestParam("page") String page);

    @GetMapping("/loans/extend/{id}")
    ResponseEntity<LoanBean> extendLoan(@PathVariable("id") long id);

}
