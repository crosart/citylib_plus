package com.citylib.citylibbatch.proxies;

import com.citylib.citylibbatch.beans.BookBean;
import com.citylib.citylibbatch.beans.LoanBean;
import com.citylib.citylibbatch.beans.UserBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

}
