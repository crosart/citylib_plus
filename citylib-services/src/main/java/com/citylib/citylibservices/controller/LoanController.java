package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.config.PropertiesConfig;
import com.citylib.citylibservices.exception.MaxedException;
import com.citylib.citylibservices.exception.NotFoundException;
import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.model.Reservation;
import com.citylib.citylibservices.model.User;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.LoanRepository;
import com.citylib.citylibservices.repository.ReservationRepository;
import com.citylib.citylibservices.repository.UserRepository;
import com.citylib.citylibservices.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller centralizing all the loan related operations.
 *
 * @author crosart
 */
@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PropertiesConfig appProperties;
    @Autowired
    private LoanService loanService;

    /**
     * Retrieves current unreturned due loans.
     *
     * @return The list of unreturned due loans.
     */
    @GetMapping("/due")
    public List<Loan> getCurrentDueLoans() {
        return loanRepository.findByDueLessThanEqualAndReturnedFalse(LocalDate.now());
    }

    @GetMapping("/book/{id}")
    public List<Loan> getLoansListByBookId(@PathVariable("id") long id) {
        return loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(id);
    }

    /**
     * Retrieves all loans, past and present, for a specified user.
     *
     * @param id ID of the user.
     * @param page Current page number.
     * @return The pageable list of loans.
     */
    @GetMapping("/user/{id}")
    public Page<Loan> getUserLoansByUserId(@PathVariable long id, @RequestParam("page") String page) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, appProperties.getDefaultPageSize(), Sort.by("id").descending());
        return loanRepository.findByUserId(id, pageable);
    }

    /**
     * Extends the selected loan due date by 4 weeks, only if it hasn't already been extended.
     *
     * @param id
     * @return
     */
    // TODO
    @GetMapping("/extend/{id}")
    public ResponseEntity<Loan> extendLoan(@PathVariable("id") long id) {
        Optional<Loan> loanData = loanRepository.findById(id);
        if (loanData.get().getDue().isAfter(LocalDate.now())) {
            if (loanData.isPresent() && !loanData.get().isExtended()) {
                Loan _loan = loanData.get();
                _loan.setDue(loanData.get().getDue().plusWeeks(4));
                _loan.setExtended(true);
                return new ResponseEntity<>(loanRepository.save(_loan), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
    }

    @GetMapping("/add/user/{userId}/book/{bookId}")
    public void addLoan(@PathVariable("bookId") long bookId, @PathVariable("userId") long userId) throws NotFoundException, MaxedException {
        Loan newLoan = new Loan();
        Optional<Book> loanedBook = bookRepository.findById(bookId);
        Optional<User> loaningUser = userRepository.findById(userId);
        if (loanedBook.isPresent()) {
            newLoan.setBook(loanedBook.get());
            if (loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(bookId).size() < loanedBook.get().getQuantity()) {
                newLoan.setDue(LocalDate.now().plusWeeks(4));
                if (loaningUser.isPresent()) {
                    newLoan.setUser(loaningUser.get());
                    loanRepository.save(newLoan);
                    Optional<Reservation> existingReservation = reservationRepository.getByBook_IdAndUser_Id(bookId, userId);
                    existingReservation.ifPresent(reservation -> reservationRepository.deleteById(reservation.getId()));
                } else {
                    throw new NotFoundException("L'utilisateur demandé n'existe pas. ID=" + userId);
                }
            } else {
                throw new MaxedException("Tous les exemplaires du livre demandé sont actuellement empruntés, emprunt impossible. ID=" + bookId);
            }
        } else {
            throw new NotFoundException("Le livre demandé n'existe pas. ID=" + bookId);
        }
    }

    @GetMapping("/return/{id}")
    public void returnLoan(@PathVariable("loanId") long loanId) throws NotFoundException {
        Optional<Loan> requestedLoan = loanRepository.findById(loanId);
        if (!requestedLoan.isPresent()) {
            throw new NotFoundException("L'emprunt demandé n'existe pas. ID=" + loanId);
        } else {
            List<Loan> bookLoans = this.getLoansListByBookId(requestedLoan.get().getBook().getId());
            requestedLoan.get().setReturned(true);
            loanRepository.save(requestedLoan.get());
            // TODO DECLENCHER ENVOI MAIL RESERVATION
            if (bookLoans.size() == requestedLoan.get().getBook().getQuantity()) {
                loanService.sendMailToFirstReservation(requestedLoan.get().getBook().getId());
            }
        }
    }

}
