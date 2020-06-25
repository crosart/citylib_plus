package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.config.PropertiesConfig;
import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller centralizing all the book related operations.
 *
 * @author crosart
 */
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private PropertiesConfig appProperties;

    /**
     * Retrieves the last "X" books added to the database.
     * <p>
     * The number of books returned is set by the "lastBooks" property from {@link PropertiesConfig}.
     * </p>
     *
     * @return the pageable list of the "X" last books.
     */
    @GetMapping()
    public Page<Book> getLastBooks() {
        Pageable pageable = PageRequest.of(0, appProperties.getLastBooks(), Sort.by("id").descending());
        return bookRepository.findAll(pageable);
    }

    /**
     * Retrieves the entire list of books from the database.
     * <p>
     * The number of books per page is set by the "defaultPageSize" property from {@link PropertiesConfig}.
     * </p>
     *
     * @param page Current page number.
     * @return The pageable list of books.
     */
    @GetMapping("/all")
    public Page<Book> getAllBooks(@RequestParam("page") String page) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, appProperties.getDefaultPageSize(), Sort.by("title").ascending());
        return bookRepository.findAll(pageable);
    }

    /**
     * Retrieves the book corresponding to the served id.
     *
     * @param id ID of the book.
     * @return The retrieved book (or empty object if not found).
     */
    @GetMapping("/id/{id}")
    public Optional<Book> getBookById(@PathVariable long id) {
        Optional<Book> book = bookRepository.findById(id);
        List<Loan> activeLoans = loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(id);
        book.get().setAvailable(book.get().getQuantity() - activeLoans.size());
        return book;
    }

    /**
     * Retrieves the list of books corresponding to the query.
     * Searches for occurrences of the query in ISBN, Title ot Author.
     *
     * @param query Researched string.
     * @param page Current page number.
     * @return The pageable list of books.
     */
    @GetMapping("/search")
    public Page<Book> getBooksByQuery(@RequestParam("query") String query, @RequestParam("page") String page) {

        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, appProperties.getDefaultPageSize(), Sort.by("title").ascending());
        return bookRepository.findByIsbnContainingIgnoreCaseOrTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query, query, pageable);
    }

    /*
    @PostMapping
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
    */

    /*
    @DeleteMapping(/{id})
    public void deleteBook(@PathVariable long id) {
        bookRepository.deleteById(id);
    }
    */

    /*
    @PutMapping
    public void updateBook(@RequestBody Book book) {
        bookRepository.save(book);
    }
    */

}
