package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.config.PropertiesConfig;
import com.citylib.citylibservices.dto.LoanDto;
import com.citylib.citylibservices.exception.MaxedException;
import com.citylib.citylibservices.exception.NotFoundException;
import com.citylib.citylibservices.model.*;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.LoanRepository;
import com.citylib.citylibservices.repository.ReservationRepository;
import com.citylib.citylibservices.repository.UserRepository;
import com.citylib.citylibservices.service.MailService;
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
    private MailService mailService;

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
     * @param id ID of the loan
     * @return HTTP Response Code
     */
    @GetMapping("/extend/{id}")
    public ResponseEntity<Loan> extendLoan(@PathVariable("id") long id) {
        Optional<Loan> loanData = loanRepository.findById(id);
        if (loanData.isPresent()) {
            if (loanData.get().getDue().isAfter(LocalDate.now()) && !loanData.get().isExtended()) {
                Loan currentLoan = loanData.get();
                currentLoan.setDue(loanData.get().getDue().plusWeeks(4));
                currentLoan.setExtended(true);
                return new ResponseEntity<>(loanRepository.save(currentLoan), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a new loan.
     *
     * @param loanDto Dto Object containing book and user id
     * @throws NotFoundException throw NotFoundException if user or book doesn't exist
     * @throws MaxedException throw MaxedException if all books are loaned
     */
    @PostMapping("/add")
    public void addLoan(@RequestBody LoanDto loanDto) throws NotFoundException, MaxedException {
        Loan newLoan = new Loan();
        Optional<Book> loanedBook = bookRepository.findById(loanDto.getBookId());
        Optional<User> loaningUser = userRepository.findById(loanDto.getUserId());
        if (loanedBook.isPresent()) {
            newLoan.setBook(loanedBook.get());
            if (loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(loanDto.getBookId()).size() < loanedBook.get().getQuantity()) {
                newLoan.setDue(LocalDate.now().plusWeeks(4));
                if (loaningUser.isPresent()) {
                    newLoan.setUser(loaningUser.get());
                    loanRepository.save(newLoan);
                    Optional<Reservation> existingReservation = reservationRepository.getByBook_IdAndUser_Id(loanDto.getBookId(), loanDto.getUserId());
                    existingReservation.ifPresent(reservation -> reservationRepository.deleteById(reservation.getId()));
                } else {
                    throw new NotFoundException("L'utilisateur demandé n'existe pas. ID=" + loanDto.getUserId());
                }
            } else {
                throw new MaxedException("Tous les exemplaires du livre demandé sont actuellement empruntés, emprunt impossible. ID=" + loanDto.getBookId());
            }
        } else {
            throw new NotFoundException("Le livre demandé n'existe pas. ID=" + loanDto.getBookId());
        }
    }

    /**
     * Mark a loan as returned if the book is returned by the user.
     *
     * @param loanId ID of the returning loan
     * @throws NotFoundException throw NotFoundException if loan doesn't exist
     */
    @PutMapping("/{loanId}/return")
    public void returnLoan(@PathVariable("loanId") long loanId) throws NotFoundException {
        Optional<Loan> requestedLoan = loanRepository.findById(loanId);
        if (!requestedLoan.isPresent()) {
            throw new NotFoundException("L'emprunt demandé n'existe pas. ID=" + loanId);
        } else {
            List<Loan> bookLoans = this.getLoansListByBookId(requestedLoan.get().getBook().getId());
            requestedLoan.get().setReturned(true);
            loanRepository.save(requestedLoan.get());

            if (bookLoans.size() == requestedLoan.get().getBook().getQuantity()) {
                Optional<Reservation> reservation = reservationRepository.findFirstByBook_IdOrderByIdAsc(requestedLoan.get().getBook().getId());
                if (reservation.isPresent() && reservation.get().getNotificationDate() == null) {
                    Mail mail = new Mail();

                    mail.setRecipient(reservation.get().getUser().getEmail());
                    mail.setSubject("CityLib : Un livre que vous avez réservé est disponible !");
                    mail.setBody(reservation.get().getBook().getTitle() + "est disponible ! Venez le récupérer à la bibliothèque sous 48H !");
                    mailService.sendMail(mail);
                }
            }
        }
    }

}
