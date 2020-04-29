package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LoanRepository loanRepository;

    @GetMapping(value = "/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping(value = "/books/{id}")
    public Optional<Book> getBookById(@PathVariable long id) {
        Optional<Book> book = bookRepository.findById(id);
        List<Loan> activeLoans = loanRepository.findByBookIdAndReturnedFalse(id);
        
        book.get().setAvailable(book.get().getQuantity() - activeLoans.size());

        return book;
    }

    @PostMapping(value = "/books")
    public ResponseEntity<Void> addBook(@RequestBody Book book) {
        Book addedBook = bookRepository.save(book);
        if (addedBook == null) {
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedBook.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/books/{id}")
    public void deleteBook(@PathVariable long id) {
        bookRepository.deleteById(id);
    }

    @PutMapping(value = "/books")
    public void updateBook(@RequestBody Book book) {
        bookRepository.save(book);
    }

}
