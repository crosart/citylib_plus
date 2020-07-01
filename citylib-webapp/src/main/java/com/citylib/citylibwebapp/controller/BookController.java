package com.citylib.citylibwebapp.controller;

import com.citylib.citylibwebapp.model.BookBean;
import com.citylib.citylibwebapp.model.LoanBean;
import com.citylib.citylibwebapp.model.ReservationBean;
import com.citylib.citylibwebapp.model.UserBean;
import com.citylib.citylibwebapp.proxy.CitylibServicesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
     * Retrieve the properties of a requested book.
     *
     * @param id ID of the book.
     * @param model Parameters to pass to the webpage.
     * @return The template name.
     */
    @GetMapping("/id/{id}")
    public String bookDetail(@PathVariable("id") int id, Model model, Principal principal) {
        BookBean book = servicesProxy.getBookById(id);
        List<ReservationBean> listReservations = servicesProxy.getReservationsListByBookId(id);
        List<LoanBean> listLoans = servicesProxy.getLoansListByBookId(id);
        if (principal != null) {
            UserBean loggedUser = servicesProxy.getUserByEmail(principal.getName());
            book.setReserved(listReservations.stream().map(ReservationBean::getUser).mapToLong(UserBean::getId).anyMatch(Long.valueOf(loggedUser.getId()) :: equals));
            book.setLoaned(listLoans.stream().map(LoanBean::getUser).mapToLong(UserBean::getId).anyMatch(Long.valueOf(loggedUser.getId()) :: equals));
        }
        model.addAttribute("book", book);
        model.addAttribute("listReservations", listReservations);
        model.addAttribute("listLoans", listLoans);
        return "book";
    }

}
