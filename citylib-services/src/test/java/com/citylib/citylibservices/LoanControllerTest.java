package com.citylib.citylibservices;

import com.citylib.citylibservices.config.PropertiesConfig;
import com.citylib.citylibservices.controller.LoanController;
import com.citylib.citylibservices.dto.LoanDto;
import com.citylib.citylibservices.exception.MaxedException;
import com.citylib.citylibservices.exception.NotFoundException;
import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.model.Mail;
import com.citylib.citylibservices.model.Reservation;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.LoanRepository;
import com.citylib.citylibservices.repository.ReservationRepository;
import com.citylib.citylibservices.repository.UserRepository;
import com.citylib.citylibservices.service.MailService;
import com.citylib.citylibservices.sources.ObjectBuilder;
import com.citylib.citylibservices.sources.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest extends AbstractControllerTest {

    @MockBean
    private LoanRepository loanRepository;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ReservationRepository reservationRepository;
    @MockBean
    private PropertiesConfig appProperties;
    @MockBean
    private MailService mailService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ObjectBuilder objectBuilder;

    @BeforeEach
    void initObjectBuilder() {
        objectBuilder = new ObjectBuilder();
    }

    @Test
    @DisplayName("GET / Returns due loans")
    void getDueLoans_shouldReturnListOfDueLoans() throws Exception {
        int vDueLoansListSize = 10;
        List<Loan> vDueLoansList = objectBuilder.dueLoansList(vDueLoansListSize);
        when(loanRepository.findByDueLessThanEqualAndReturnedFalse(any(LocalDate.class))).thenReturn(vDueLoansList);

        String body = mockMvc.perform(get("/loans/due"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Loan> result = objectMapper.readValue(body, new TypeReference<List<Loan>>() {});

        assertThat(result.size()).isEqualTo(vDueLoansListSize);
        assertThat(result.get(0).getDue()).isBefore(LocalDate.now());
    }

    @Test
    @DisplayName("GET / Returns active loans for chosen book ID")
    void givenBookId_shouldReturnLoansList() throws Exception {
        int vBookId = 1;
        int vLoansListSize = 1;
        List<Loan> vLoansList = objectBuilder.loansList(vLoansListSize);
        when(loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(any(Long.class))).thenReturn(vLoansList);

        String body = mockMvc.perform(get("/loans/book/{id}", vBookId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Loan> result = objectMapper.readValue(body, new TypeReference<List<Loan>>() {});

        assertThat(result.size()).isEqualTo(vLoansListSize);
        assertThat(result.get(0).getBook().getId()).isEqualTo(vBookId);
    }

    @Test
    @DisplayName("GET / Returns all loans for user ID")
    void givenUserId_shouldReturnLoansList() throws Exception {
        long vUserId = 1;
        int vLoansListSize = 3;
        int vMaxLoansPerPage = 2;
        Page<Loan> vLoansPage = new PageImpl<>(objectBuilder.loansList(vLoansListSize), PageRequest.of(0, vMaxLoansPerPage, Sort.by("id").descending()), vLoansListSize);
        when(appProperties.getDefaultPageSize()).thenReturn(vMaxLoansPerPage);
        when(loanRepository.findByUserId(any(Long.class), any(Pageable.class))).thenReturn(vLoansPage);

        String body = mockMvc.perform(get("/loans/user/{id}", vUserId)
                .param("page", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Page<Loan> result = objectMapper.readValue(body, new TypeReference<RestResponsePage<Loan>>() {});

        assertThat(result.getNumberOfElements()).isEqualTo(vLoansListSize);
        assertThat(result.getSize()).isEqualTo(vMaxLoansPerPage);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("GET / Extend loan / Return HTTP-404 if loan ID doesn't exist")
    void givenLoanId_whenLoanNotExists_shouldRespondHttpNotFound() throws Exception {
        Optional<Loan> vLoan = Optional.empty();
        long vLoanId = 1;
        when(loanRepository.findById(any(Long.class))).thenReturn(vLoan);

        String body = mockMvc.perform(get("/loans/extend/{id}", vLoanId))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertThat(body).isEmpty();
    }

    @Test
    @DisplayName("GET / Extend loan / Return HTTP-404 if loan ID doesn't exist")
    void givenLoanId_whenLoanDueDateIsBeforeNow_shouldRespondHttpNotFound() throws Exception {
        Optional<Loan> vLoan = Optional.ofNullable(objectBuilder.loan());

        assumeThat(vLoan).isPresent();

        vLoan.get().setDue(LocalDate.now().minusWeeks(1));
        long vLoanId = 1;
        when(loanRepository.findById(any(Long.class))).thenReturn(vLoan);

        String body = mockMvc.perform(get("/loans/extend/{id}", vLoanId))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertThat(body).isEmpty();
    }

    @Test
    @DisplayName("GET / Extend loan / Add 4 weeks to loan due date and set loan to extended")
    void givenLoanId_whenLoanExistsAndDueDateIsAfterNow_shouldIncrementDueDateAndSetExtendedTrue() throws Exception {
        Optional<Loan> vLoan = Optional.ofNullable(objectBuilder.loan());
        long vLoanId = 1;
        when(loanRepository.findById(any(Long.class))).thenReturn(vLoan);

        assumeThat(vLoan).isPresent();
        LocalDate previousDueDate = vLoan.get().getDue();

        mockMvc.perform(get("/loans/extend/{id}", vLoanId))
                .andExpect(status().isOk());

        // Loan result = objectMapper.readValue(body, new TypeReference<Loan>() {});

        assertThat(vLoan.get().getDue()).isEqualTo(previousDueDate.plusWeeks(4));
        assertThat(vLoan.get().isExtended()).isTrue();
        assertThat(vLoan.get().getId()).isEqualTo(vLoanId);
    }

    @Test
    @DisplayName("POST / Add loan / Return HTTP-404 if book ID doesn't exist")
    void givenLoanDtoObject_whenLoanedBookNotExists_shouldReturnNotFoundException() throws Exception {
        LoanDto vLoanDto = new LoanDto();
        vLoanDto.setBookId(1);
        vLoanDto.setUserId(1);
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(objectBuilder.user()));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/loans/add")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(asJsonString(vLoanDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("Le livre demandé n'existe pas. ID=" + vLoanDto.getBookId(), Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    @DisplayName("POST / Add loan / Return HTTP-412 if book availability maxxed")
    void givenLoanDtoObject_whenAllBooksLoaned_shouldReturnMaxedException() throws Exception {
        LoanDto vLoanDto = new LoanDto();
        vLoanDto.setBookId(1);
        vLoanDto.setUserId(1);
        Optional<Book> vBook = Optional.ofNullable(objectBuilder.book());
        assumeThat(vBook).isPresent();
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(objectBuilder.book()));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(objectBuilder.user()));
        when(loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(any(Long.class))).thenReturn(objectBuilder.loansList(vBook.get().getQuantity()));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/loans/add")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(asJsonString(vLoanDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isPreconditionFailed())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MaxedException))
                .andExpect(result -> assertEquals("Tous les exemplaires du livre demandé sont actuellement empruntés, emprunt impossible. ID=" + vLoanDto.getBookId(), Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    @DisplayName("POST / Add loan / Return HTTP-404 if user ID doesn't exist")
    void givenLoanDtoObject_whenUserNotExists_shouldReturnNotFoundException() throws Exception {
        LoanDto vLoanDto = new LoanDto();
        vLoanDto.setBookId(1);
        vLoanDto.setUserId(1);
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(objectBuilder.book()));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/loans/add")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(asJsonString(vLoanDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("L'utilisateur demandé n'existe pas. ID=" + vLoanDto.getUserId(), Objects.requireNonNull(result.getResolvedException()).getMessage()));


    }

    @Test
    @DisplayName("POST / Add loan / Create loan if conditions met")
    void givenLoanDtoObject_whenUserAndBookExistsAndBookAvailable_shouldCreateNewLoan() throws Exception {
        LoanDto vLoanDto = new LoanDto();
        vLoanDto.setBookId(1);
        vLoanDto.setUserId(1);
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(objectBuilder.book()));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(objectBuilder.user()));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/loans/add")
                .content(asJsonString(vLoanDto))
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT / Return loan / Return NotFoundException if loan ID doesn't exist")
    void givenLoanId_whenLoanNotExists_shouldThrowNotFoundException() throws Exception {
        long vLoanId = 1;
        when(loanRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .put("/loans/{loanid}/return", vLoanId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("L'emprunt demandé n'existe pas. ID=" + vLoanId, Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    @DisplayName("PUT / Return loan / Send mail and set returned to true")
    void givenLoanId_whenLoanExists_sendMail() throws Exception {
        long vLoanId = 1;
        Loan vLoan = objectBuilder.loan();
        Reservation vReservation = objectBuilder.reservation();
        assumeThat(vLoan).isNotNull();
        when(loanRepository.findById(vLoanId)).thenReturn(Optional.of(vLoan));
        when(loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(vLoan.getBook().getId())).thenReturn(objectBuilder.loansList(vLoan.getBook().getQuantity()));
        when(reservationRepository.findFirstByBook_IdOrderByIdAsc(vLoan.getBook().getId())).thenReturn(Optional.ofNullable(vReservation));

        mockMvc.perform(MockMvcRequestBuilders
                .put("/loans/{loanid}/return", vLoanId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);

        verify(mailService, times(1)).sendMail(captor.capture());
        assertThat(captor.getAllValues()).hasSize(1);
        assertThat(captor.getValue().getSubject()).contains("CityLib");

    }

}
