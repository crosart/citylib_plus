package com.citylib.citylibwebapp.proxy;

import com.citylib.citylibwebapp.dto.ReservationDto;
import com.citylib.citylibwebapp.model.BookBean;
import com.citylib.citylibwebapp.model.LoanBean;
import com.citylib.citylibwebapp.model.ReservationBean;
import com.citylib.citylibwebapp.model.UserBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * OpenFeign Proxy to bridge towards citylib-services service.
 *
 * @author crosart
 */
@FeignClient(name = "citylib-services")
public interface CitylibServicesProxy {

    // Books

    @GetMapping("/books")
    Page<BookBean> getLastBooks();

    @GetMapping("/books/all")
    Page<BookBean> getAllBooks(@RequestParam("page") String page);

    @GetMapping("books/id/{id}")
    BookBean getBookById(@PathVariable("id") long id);

    @GetMapping("books/search")
    Page<BookBean> getBooksByQuery(@RequestParam("query") String query, @RequestParam("page") String page);

    // Users

    @RequestMapping("/users/email/{email}/")
    UserBean getUserByEmail(@PathVariable("email") String email);

    @PostMapping("users/register")
    UserBean registerUserAccount(UserBean user);

    // Loans

    @GetMapping("/loans/user/{id}")
    Page<LoanBean> getUserLoans(@PathVariable("id") long id, @RequestParam("page") String page);

    @GetMapping("/loans/extend/{id}")
    ResponseEntity<LoanBean> extendLoan(@PathVariable("id") long id);

    @GetMapping("/loans/book/{id}")
    List<LoanBean> getLoansListByBookId(@PathVariable("id") long id );

    // Reservations

    @GetMapping("/reservations/book/{id}")
    List<ReservationBean> getReservationsListByBookId(@PathVariable("id") long id);

    @GetMapping("/reservations/user/{id}")
    List<ReservationBean> getReservationsListByUserId(@PathVariable("id") long id);

    @GetMapping("/reservations/book/{bookId}/reservation/{reservationId}")
    long countForwardReservations(@PathVariable("bookId") long bookId, @PathVariable("reservationId") long reservationId);

    @PostMapping("/reservations/reservation/add")
    ReservationDto addNewReservation(ReservationDto reservationDto);

    @DeleteMapping("/reservations/delete/{id}")
    void deleteReservation(@PathVariable("id") long id);

    @GetMapping("/reservations/reservation/{id}")
    Optional<ReservationBean> getReservationById(@PathVariable("id") long id);

}
