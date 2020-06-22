package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.dto.ReservationDto;
import com.citylib.citylibservices.exception.MaxedReservationsException;
import com.citylib.citylibservices.exception.NotFoundException;
import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Reservation;
import com.citylib.citylibservices.model.User;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.ReservationRepository;
import com.citylib.citylibservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/book/{id}")
    public List<Reservation> getReservationsByBookId(@PathVariable long id) {
        return reservationRepository.findByBook_IdOrderByIdAsc(id);
    }

    @GetMapping("/user/{id}")
    public List<Reservation> getReservationsByUserId(@PathVariable long id) {
        return reservationRepository.findByUser_IdOrderByIdAsc(id);
    }

    @PostMapping("/reservation/add")
    public ResponseEntity<Reservation> addReservationOfBookForUser(@RequestBody ReservationDto reservationDto) throws NotFoundException, MaxedReservationsException {
        Reservation newReservation = null;
        Book requestedBook = bookRepository.findById(reservationDto.getBookId()).orElseThrow(() -> new NotFoundException("Le livre demandé n'existe pas. ID = " + reservationDto.getBookId()));
        User requestingUser = userRepository.findById(reservationDto.getUserId()).orElseThrow(() -> new NotFoundException("L'utilisateur n'existe pas"));
        if (reservationRepository.countByBook_Id(reservationDto.getBookId()) < (requestedBook.getQuantity() * 2)) {
            newReservation.setBook(requestedBook);
            newReservation.setUser(requestingUser);
            reservationRepository.save(newReservation);
        } else {
            throw new MaxedReservationsException("Le nombre maximum de réservations est atteint pour ce livre. " +
                    "Réservations MAX : " + (requestedBook.getQuantity() * 2));
        }
        return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
    }

    @DeleteMapping("/reservation/{id}")
    public void deleteReservationById(@PathVariable long id) {
        reservationRepository.deleteById(id);
    }
}
