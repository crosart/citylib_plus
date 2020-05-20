package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.model.BookBean;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebappController {

    @Autowired
    private CitylibServicesProxy servicesProxy;

    @GetMapping("/")
    public String index(Model model) {
        Page<BookBean> booksPage = servicesProxy.getLastBooks();
        List<BookBean> books = booksPage.getContent();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

}
