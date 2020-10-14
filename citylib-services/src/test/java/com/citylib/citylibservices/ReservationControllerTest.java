package com.citylib.citylibservices;

import com.citylib.citylibservices.controller.ReservationController;
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
import com.citylib.citylibservices.sources.ObjectBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
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

    @Test
    @DisplayName("GET / Return Reservations count before current reservation ID for book ID")
    void countReservations_shouldReturnNumberOfNextReservationsForBookId() throws Exception {
        long vBookId = 1;
        long vReservationId = 2;
        long vReservationsCount = 2;
        when(reservationRepository.countByBook_IdAndIdLessThan(vBookId, vReservationId)).thenReturn(vReservationsCount);

        String body = mockMvc.perform(get("/reservations/book/{bookId}/reservation/{reservationId}", vBookId, vReservationId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long result = objectMapper.readerFor(Long.class).readValue(body);

        assertThat(result).isEqualTo(2);
        verify(reservationRepository, times(1)).countByBook_IdAndIdLessThan(any(Long.class), any(Long.class));
    }

    @Test
    @DisplayName("POST / Add Reservation / Add a new reservation")
    void givenReservationDto_shouldAddReservationAndReturnCreated() throws Exception {
        ReservationDto vReservationDto = new ReservationDto();
        vReservationDto.setBookId(1);
        vReservationDto.setUserId(1);
        Optional<Book> vBook = Optional.ofNullable(objectBuilder.book());
        Optional<User> vUser = Optional.ofNullable(objectBuilder.user());
        long vReservationsCount = 4;

        assumeThat(vBook).isPresent();
        assumeThat(vUser).isPresent();

        when(bookRepository.findById(vReservationDto.getBookId())).thenReturn(vBook);
        when(userRepository.findById(vReservationDto.getUserId())).thenReturn(vUser);
        when(reservationRepository.existsByBook_IdAndUser_Id(vReservationDto.getBookId(), vReservationDto.getUserId())).thenReturn(false);
        when(reservationRepository.countByBook_Id(vReservationDto.getBookId())).thenReturn(vReservationsCount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/reservations/reservation/add")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(asJsonString(vReservationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST / Add Reservation / Should return Conflict if reservation already exists")
    void givenReservationDto_whenReservationExists_shouldReturnConflict() throws Exception {
        ReservationDto vReservationDto = new ReservationDto();
        vReservationDto.setBookId(1);
        vReservationDto.setUserId(1);
        Optional<Book> vBook = Optional.ofNullable(objectBuilder.book());
        Optional<User> vUser = Optional.ofNullable(objectBuilder.user());
        long vReservationsCount = 4;

        assumeThat(vBook).isPresent();
        assumeThat(vUser).isPresent();

        when(bookRepository.findById(vReservationDto.getBookId())).thenReturn(vBook);
        when(userRepository.findById(vReservationDto.getUserId())).thenReturn(vUser);
        when(reservationRepository.existsByBook_IdAndUser_Id(vReservationDto.getBookId(), vReservationDto.getUserId())).thenReturn(true);
        when(reservationRepository.countByBook_Id(vReservationDto.getBookId())).thenReturn(vReservationsCount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/reservations/reservation/add")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(asJsonString(vReservationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST / Add Reservation / Should return Conflict if reservation already exists")
    void givenReservationDto_whenReservationExists_shouldThrowMaxedException() throws Exception {
        ReservationDto vReservationDto = new ReservationDto();
        vReservationDto.setBookId(1);
        vReservationDto.setUserId(1);
        Optional<Book> vBook = Optional.ofNullable(objectBuilder.book());
        Optional<User> vUser = Optional.ofNullable(objectBuilder.user());
        long vReservationsCount = 10;

        assumeThat(vBook).isPresent();
        assumeThat(vUser).isPresent();

        when(bookRepository.findById(vReservationDto.getBookId())).thenReturn(vBook);
        when(userRepository.findById(vReservationDto.getUserId())).thenReturn(vUser);
        when(reservationRepository.existsByBook_IdAndUser_Id(vReservationDto.getBookId(), vReservationDto.getUserId())).thenReturn(false);
        when(reservationRepository.countByBook_Id(vReservationDto.getBookId())).thenReturn(vReservationsCount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/reservations/reservation/add")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(asJsonString(vReservationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isPreconditionFailed())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MaxedException))
                .andExpect(result -> assertEquals("Le nombre maximum de réservations est atteint pour ce livre. Réservations MAX : " + vBook.get().getQuantity() * 2, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @DisplayName("DELETE / Delete Reservation / Should delete reservation if it exists")
    void givenReservationId_shouldDeleteReservation() throws Exception {
        long vReservationId = 1;
        Reservation vReservation = objectBuilder.reservation();

        when(reservationRepository.findFirstByBook_IdOrderByIdAsc(vReservation.getBook().getId())).thenReturn(Optional.empty());
        when(reservationRepository.findById(vReservationId)).thenReturn(Optional.of(vReservation));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/reservations/{id}", vReservationId))
                .andExpect(status().isOk());

        verify(reservationRepository, times(1)).delete(any(Reservation.class));
    }

    @Test
    @DisplayName("DELETE / Delete Reservation / Should throw EntityNotFound exception if reservation does not exist")
    void givenReservationId_whenReservationNotExists_shouldThrowEntityNotFoundException() throws Exception {
        long vReservationId = 1;

        when(reservationRepository.findById(vReservationId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/reservations/{id}", vReservationId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("La réservation que vous souhaitez supprimer n'existe pas. ID=" + vReservationId, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @DisplayName("DELETE / Delete Reservation / Should send mail if NotificationDate exists and next reservation exists")
    void givenReservationId_whenNotificationDateAndNextReservationExists_shouldSendMail() throws Exception {
        long vReservationId = 1;
        Reservation vReservation = objectBuilder.reservation();
        vReservation.setNotificationDate(LocalDate.now().minusDays(1));
        Reservation vNextReservation = objectBuilder.reservation();
        vNextReservation.setId(2);

        when(reservationRepository.findById(vReservationId)).thenReturn(Optional.of(vReservation));
        when(reservationRepository.findFirstByBook_IdOrderByIdAsc(vReservation.getBook().getId())).thenReturn(Optional.of(vNextReservation));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/reservations/{id}", vReservationId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);

        verify(mailService, times(1)).sendMail(captor.capture());
        assertThat(captor.getValue().getSubject()).isEqualTo("CityLib : Un livre que vous avez réservé est disponible !");
        assertThat(captor.getValue().getBody()).isEqualTo(vReservation.getBook().getTitle() + "est disponible ! Venez le récupérer à la bibliothèque sous 48H !");
        verify(reservationRepository, times(1)).save(vNextReservation);
    }

    @Test
    @DisplayName("DELETE / Delete Reservation / Should not send mail if NotificationDate does not exist")
    void givenReservationId_whenNotificationDateNotExists_shouldNotSendMail() throws Exception {
        long vReservationId = 1;
        Reservation vReservation = objectBuilder.reservation();
        Reservation vNextReservation = objectBuilder.reservation();
        vNextReservation.setId(2);

        when(reservationRepository.findById(vReservationId)).thenReturn(Optional.of(vReservation));
        when(reservationRepository.findFirstByBook_IdOrderByIdAsc(vReservation.getBook().getId())).thenReturn(Optional.of(vNextReservation));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/reservations/{id}", vReservationId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);

        verify(mailService, times(0)).sendMail(captor.capture());
        verify(reservationRepository, times(0)).save(vNextReservation);

    }

    @Test
    @DisplayName("DELETE / Delete Reservation / Should not send mail if no next reservation does not exist")
    void givenReservationId_whenNextReservationNotExists_shouldNotSendMail() throws Exception {
        long vReservationId = 1;
        Reservation vReservation = objectBuilder.reservation();
        vReservation.setNotificationDate(LocalDate.now().minusDays(1));

        when(reservationRepository.findById(vReservationId)).thenReturn(Optional.of(vReservation));
        when(reservationRepository.findFirstByBook_IdOrderByIdAsc(vReservation.getBook().getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/reservations/{id}", vReservationId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);

        verify(mailService, times(0)).sendMail(captor.capture());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

}
