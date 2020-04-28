package com.citylib.citylibwebapp.proxies;

import com.citylib.citylibwebapp.beans.BookBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "citylib-books")
public interface CitylibBooksProxy {

    @GetMapping(value = "/books")
    List<BookBean> getAllBooks();

    @GetMapping(value = "/books/{id}")
    BookBean getBookById(@PathVariable("id") long id);

}
