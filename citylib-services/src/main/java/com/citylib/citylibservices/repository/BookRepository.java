package com.citylib.citylibservices.repository;

import com.citylib.citylibservices.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository extension for book related operations.
 *
 * @author crosart
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByIsbnContainingIgnoreCaseOrTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String isbn, String title, String author, Pageable pageable);
    Page<Book> findAll(Pageable pageable);
}
