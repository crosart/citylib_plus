package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.beans.BookBean;
import com.citylib.citylibwebapp.proxies.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class WebappController {

    @Autowired
    private CitylibServicesProxy servicesProxy;

    @GetMapping(value="/")
    public String accueil(Model model) {
        List<BookBean> books = servicesProxy.getAllBooks();

        model.addAttribute("books", books);

        return "index";
    }

    @GetMapping(value="/books/{id}")
    public String bookDetail(@PathVariable int id, Model model) {
        BookBean book = servicesProxy.getBookById(id);

        model.addAttribute("book", book);

        return "bookDetail";
    }

}
