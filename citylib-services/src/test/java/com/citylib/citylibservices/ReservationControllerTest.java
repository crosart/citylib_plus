package com.citylib.citylibservices;

import com.citylib.citylibservices.controller.ReservationController;
import com.citylib.citylibservices.model.Reservation;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.ReservationRepository;
import com.citylib.citylibservices.repository.UserRepository;
import com.citylib.citylibservices.service.MailService;
import com.citylib.citylibservices.sources.ObjectBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
public class ReservationControllerTest extends AbstractControllerTest {

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailService mailService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private ObjectBuilder objectBuilder;

    @BeforeEach
    void initObjectBuilder() {
        objectBuilder = new ObjectBuilder();
    }

    @Test
    @DisplayName("GET / Return reservation from ID")
    void givenReservationId_shouldReturnReservationObject() throws Exception {
        long vReservationId = 1;
        Optional<Reservation> vReservation = Optional.ofNullable(objectBuilder.reservation());
        when(reservationRepository.findById(vReservationId)).thenReturn(vReservation);

        assumeThat(vReservation).isPresent();

        String body = mockMvc.perform(get("/reservations/reservation/{id}", vReservationId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Reservation result = new ObjectMapper().readerFor(Reservation.class).readValue(body);

        assertThat(result).isEqualToComparingFieldByField(vReservation.get());
    }

    @Test
    @DisplayName("GET / Return reservations list from book ID")
    void givenBookId_shouldReturnReservationsList() throws Exception {
        long vBookId = 1;
        List<Reservation> vReservationsList = objectBuilder.reservationsList(2);
        when(reservationRepository.findByBook_IdOrderByIdAsc(vBookId)).thenReturn(vReservationsList);

        String body = mockMvc.perform(get("/reservations/book/{id}", vBookId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Reservation> result = objectMapper.readValue(body, new TypeReference<List<Reservation>>() {});

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getBook().getId()).isEqualTo(vBookId);
    }

    @Test
    @DisplayName("GET / Return reservations list from user ID")
    void givenUserId_shouldReturnReservationsList() throws Exception {
        long vUserId = 1;
        List<Reservation> vReservationsList = objectBuilder.reservationsList(3);
        when(reservationRepository.findByUser_IdOrderByIdAsc(vUserId)).thenReturn(vReservationsList);

        String body = mockMvc.perform(get("/reservations/user/{id}", vUserId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Reservation> result = objectMapper.readValue(body, new TypeReference<List<Reservation>>() {});

        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getUser().getId()).isEqualTo(vUserId);
    }

    @Test
    @DisplayName("GET / Return notified reservations list")
    void getNotified_shouldReturnReservationsList() throws Exception {
        List<Reservation> vReservationsList = objectBuilder.notifiedReservationsList(2);
        when(reservationRepository.getAllByNotificationDateNotNull()).thenReturn(vReservationsList);

        String body = mockMvc.perform(get("/reservations/notified"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Reservation> result = objectMapper.readValue(body, new TypeReference<List<Reservation>>() {});

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getNotificationDate()).isEqualTo(LocalDate.now().minusDays(1));

    }

    /*
    @Test
    @DisplayName("GET / Return reservations count after current reservation ID for book ID")
    void countReservations_shouldReturnNumberOfNextReservationsForBookId() throws Exception {
        List<Reservation> vReservationsList = objectBuilder.notifiedReservationsList(2);
        when(reservationRepository.getAllByNotificationDateNotNull()).thenReturn(vReservationsList);

        String body = mockMvc.perform(get("/reservations/notified"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Reservation> result = objectMapper.readValue(body, new TypeReference<List<Reservation>>() {});

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getNotificationDate()).isEqualTo(LocalDate.now().minusDays(1));

    }
*/


}
