package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.dto.ReservationDto;
import com.citylib.citylibservices.exception.MaxedException;
import com.citylib.citylibservices.exception.NotFoundException;
import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Mail;
import com.citylib.citylibservices.model.Reservation;
import com.citylib.citylibservices.model.User;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.ReservationRepository;
import com.citylib.citylibservices.repository.UserRepository;
import com.citylib.citylibservices.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller centralizing all the reservation related operations.
 *
 * @author crosart
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @GetMapping("/reservation/{id}")
    public Optional<Reservation> getReservationById(@PathVariable long id) {
        return reservationRepository.findById(id);
    }

    @GetMapping("/book/{id}")
    public List<Reservation> getReservationsByBookId(@PathVariable long id) {
        return reservationRepository.findByBook_IdOrderByIdAsc(id);
    }

    @GetMapping("/user/{id}")
    public List<Reservation> getReservationsByUserId(@PathVariable long id) {
        return reservationRepository.findByUser_IdOrderByIdAsc(id);
    }

    @GetMapping("/notified")
    public List<Reservation> getNotifiedReservations() {
        return reservationRepository.getAllByNotificationDateNotNull();
    }

    @GetMapping("/book/{bookId}/reservation/{reservationId}")
    public long countForwardReservations(@PathVariable long bookId, @PathVariable long reservationId) {
        return reservationRepository.countByBook_IdAndIdLessThan(bookId, reservationId);
    }

    @PostMapping("/reservation/add")
    public ResponseEntity<Reservation> addReservationOfBookForUser(@RequestBody ReservationDto reservationDto) throws NotFoundException, MaxedException {
        Reservation newReservation = new Reservation();
        Book requestedBook = bookRepository.findById(reservationDto.getBookId()).orElseThrow(() -> new NotFoundException("Le livre demandé n'existe pas. ID = " + reservationDto.getBookId()));
        User requestingUser = userRepository.findById(reservationDto.getUserId()).orElseThrow(() -> new NotFoundException("L'utilisateur n'existe pas"));
        newReservation.setBook(requestedBook);
        newReservation.setUser(requestingUser);
        if (reservationRepository.existsByBook_IdAndUser_Id(reservationDto.getBookId(), reservationDto.getUserId())) {
            return new ResponseEntity<>(newReservation, HttpStatus.CONFLICT);
        } else {
            if (reservationRepository.countByBook_Id(reservationDto.getBookId()) < (requestedBook.getQuantity() * 2)) {
                reservationRepository.save(newReservation);
            } else {
                throw new MaxedException("Le nombre maximum de réservations est atteint pour ce livre. " +
                        "Réservations MAX : " + (requestedBook.getQuantity() * 2));
            }
            return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteReservationById(@PathVariable long id) {
        Reservation reservation = reservationRepository.getOne(id);
        reservationRepository.delete(reservation);
        Optional<Reservation> nextReservation = reservationRepository.findFirstByBook_IdOrderByIdAsc(reservation.getBook().getId());
        if (reservation.getNotificationDate() != null && nextReservation.isPresent()) {
            Mail mail = new Mail();
            mail.setRecipient(nextReservation.get().getUser().getEmail());
            mail.setSubject("CityLib : Un livre que vous avez réservé est disponible !");
            mail.setBody(reservation.getBook().getTitle() + "est disponible ! Venez le récupérer à la bibliothèque sous 48H !");
            mailService.sendMail(mail);
        } else {
            return;
        }

    }
}
