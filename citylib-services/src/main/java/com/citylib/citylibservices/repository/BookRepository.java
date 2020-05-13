package com.citylib.citylibservices.repository;

import com.citylib.citylibservices.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByYear(int year);
    List<Book> findByAuthor(Pageable page, int year);
    //TODO Pagination
}
