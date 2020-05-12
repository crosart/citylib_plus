package com.citylib.citylibwebapp.controllers;

import com.citylib.citylibwebapp.beans.Book;
import com.citylib.citylibwebapp.beans.User;
import com.citylib.citylibwebapp.dto.UserDto;
import com.citylib.citylibwebapp.proxies.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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



}
