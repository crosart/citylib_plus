package com.citylib.citylibservices.repository;

import com.citylib.citylibservices.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JpaRepository extension for reservation related operations.
 *
 * @author crosart
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByBook_IdOrderByIdAsc(long bookId);
    List<Reservation> findByUser_IdOrderByIdAsc(long userId);
    boolean existsByBook_IdAndUser_Id(long bookId, long userId);
    long countByBook_Id(long bookId);
    List<Reservation> getAllByNotificationDateNotNull();
    Optional<Reservation> getByBook_IdAndUser_Id(long bookId, long userId);
}
