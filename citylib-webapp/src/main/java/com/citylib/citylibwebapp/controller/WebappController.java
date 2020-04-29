package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.beans.BookBean;
import com.citylib.citylibwebapp.proxies.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class WebappController {

    @Autowired
    private CitylibServicesProxy ServicesProxy;

    @RequestMapping("/")
    public String accueil(Model model) {
        List<BookBean> books = ServicesProxy.getAllBooks();

        model.addAttribute("books", books);

        return "index";
    }

    @RequestMapping("/book-details/{id}")
    public String bookDetail(@PathVariable int id, Model model) {
        BookBean book = ServicesProxy.getBookById(id);

        model.addAttribute("book", book);

        return "bookDetail";
    }

}
