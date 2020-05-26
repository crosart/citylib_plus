package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.model.BookBean;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Service controller for book related operations.
 *
 * @author crosart
 */
@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private CitylibServicesProxy servicesProxy;

    /**
     * Retrieves the list of books corresponding to the query.
     * Searches for occurrences of the query in ISBN, Title ot Author through the backend service.
     *
     * @param request Parameters passed through {@link HttpServletRequest}.
     * @param model Parameters to pass to the webpage.
     * @return The template name.
     */
    @GetMapping("/search")
    public String bookSearch(HttpServletRequest request, Model model) {
        Page<BookBean> books = servicesProxy.getBooksByQuery(request.getParameter("query"), request.getParameter("page"));
        model.addAttribute("books", books);
        return "search_results";
    }

    /**
     * Retrieved the properties of a requested book.
     *
     * @param id ID of the book.
     * @param model Parameters to pass to the webpage.
     * @return The template name.
     */
    @GetMapping("/id/{id}")
    public String bookDetail(@PathVariable int id, Model model) {
        BookBean book = servicesProxy.getBookById(id);
        model.addAttribute("book", book);
        return "book";
    }

}
