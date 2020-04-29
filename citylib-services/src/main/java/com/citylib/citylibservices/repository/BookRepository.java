package com.citylib.citylibservices.repository;

import com.citylib.citylibservices.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCollectionContaining(String collection);
    List<Book> findByYear(int year);
    List<Book> findByAvailabilityGreaterThan(int available);
}
