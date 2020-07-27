package com.citylib.citylibbatch.proxies;

import com.citylib.citylibbatch.beans.BookBean;
import com.citylib.citylibbatch.beans.LoanBean;
import com.citylib.citylibbatch.beans.ReservationBean;
import com.citylib.citylibbatch.beans.UserBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * OpenFeign Proxy to bridge towards citylib-services service.
 *
 * @author crosart
 */
@FeignClient(name = "citylib-services")
public interface CitylibServicesProxy {

    @GetMapping(value = "/books")
    List<BookBean> getAllBooks();

    @GetMapping(value = "/books/id/{id}")
    BookBean getBookById(@PathVariable("id") long id);

    @GetMapping(value = "/loans/due")
    List<LoanBean> getCurrentDueLoans();

    @GetMapping(value = "/users/id/{id}")
    UserBean getUserById(@PathVariable("id") long id);

    @DeleteMapping(value = "/reservations/delete/{id}")
    void deleteReservation(@PathVariable("id") long id);

    @GetMapping(value = "/reservations/notified")
    List<ReservationBean> getNotifiedReservations();

    @PostMapping(value = "/mail/due")
    void sendMailForDueLoans(List<LoanBean> dueLoans);

}
