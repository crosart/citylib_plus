package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.model.Book;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class WebappController {

    @Autowired
    private CitylibServicesProxy servicesProxy;

    @GetMapping("/")
    public String index(Model model) {
        List<Book> books = servicesProxy.getAllBooks();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable int id, Model model) {
        Book book = servicesProxy.getBookById(id);
        model.addAttribute("book", book);
        return "bookDetail";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }



}
