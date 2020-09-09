package com.citylib.citylibservices;

import com.citylib.citylibservices.config.PropertiesConfig;
import com.citylib.citylibservices.controller.BookController;
import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.repository.BookRepository;
import com.citylib.citylibservices.repository.LoanRepository;
import com.citylib.citylibservices.sources.ObjectBuilder;
import com.citylib.citylibservices.sources.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class)
public class BookControllerTest extends AbstractControllerTest {

    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private LoanRepository loanRepository;
    @MockBean
    private PropertiesConfig appProperties;

    @Autowired
    MockMvc mockMvc;

    private ObjectBuilder objectBuilder;

    @BeforeEach
    void initObjectBuilder() {
        objectBuilder = new ObjectBuilder();
    }

    @Test
    void getLastBooks_shouldReturnListOfXBooks() throws Exception {
        int vBooksListSize = 10;
        int vMaxBooks = 3;
        Page<Book> vBookPage = new PageImpl<>(objectBuilder.booksList(vBooksListSize), PageRequest.of(0, vMaxBooks, Sort.by("id").descending()), vBooksListSize);
        when(appProperties.getLastBooks()).thenReturn(vMaxBooks);
        when(bookRepository.findAll(vBookPage.getPageable())).thenReturn(vBookPage);

        String body = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Page<Book> result = new ObjectMapper().readValue(body, new TypeReference<RestResponsePage<Book>>() {});

        assertThat(result.getSize()).isEqualTo(vMaxBooks);
        assertThat(result.iterator().next()).isInstanceOf(Book.class);
        //TODO
        assertThat(result).usingRecursiveFieldByFieldElementComparator().isEqualTo(vBookPage);
    }

    @Test
    void getAllBooksPage1_shouldReturnListOf5LastBooks() throws Exception {
        int vBooksListSize = 10;
        int vMaxBooksPerPage = 7;
        Page<Book> vBookPage = new PageImpl<>(objectBuilder.booksList(vBooksListSize), PageRequest.of(0, vMaxBooksPerPage, Sort.by("title").descending()), vBooksListSize);
        when(appProperties.getDefaultPageSize()).thenReturn(vMaxBooksPerPage);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(vBookPage);

        String body = mockMvc.perform(get("/books/all")
                .param("page", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Page<Book> result = new ObjectMapper().readValue(body, new TypeReference<RestResponsePage<Book>>() {});

        assertThat(result.getNumberOfElements()).isEqualTo(vBooksListSize);
        assertThat(result.iterator().next()).isInstanceOf(Book.class);
        assertThat(result.getSize()).isEqualTo(vMaxBooksPerPage);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result).usingRecursiveFieldByFieldElementComparator().isEqualTo(vBookPage);
    }

    @Test
    void givenId_shouldReturnBookObject() throws Exception {
        long vBookId = 1;
        int vLoansListSize = 2;
        Optional<Book> vBook = Optional.ofNullable(objectBuilder.book());
        when(bookRepository.findById(vBookId)).thenReturn(vBook);
        when(loanRepository.findByBookIdAndReturnedFalseOrderByDueAsc(vBookId)).thenReturn(objectBuilder.loansList(vLoansListSize));

        //TODO
        assumeThat(vBook).isPresent();

        String body = mockMvc.perform(get("/books/id/{id}", vBookId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Book result = new ObjectMapper().readerFor(Book.class).readValue(body);

        vBook.get().setAvailable(result.getQuantity() - vLoansListSize);
        assertThat(result).isEqualToComparingFieldByField(vBook.get());
    }

    @Test
    void givenQueryString_shouldReturnBooksMList() throws Exception {
        String vQuery = "titre";
        int vBooksListSize = 10;
        int vMaxBooksPerPage = 5;

        Page<Book> vBookPage = new PageImpl<>(objectBuilder.booksList(vBooksListSize), PageRequest.of(0, vMaxBooksPerPage, Sort.by("id").descending()), vBooksListSize);
        when(appProperties.getDefaultPageSize()).thenReturn(vMaxBooksPerPage);
        when(bookRepository.findByIsbnContainingIgnoreCaseOrTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(vBookPage);

        String body = mockMvc.perform(get("/books/search")
                .param("query", vQuery)
                .param("page", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Page<Book> result = new ObjectMapper().readValue(body, new TypeReference<RestResponsePage<Book>>() {});

        assertThat(result.getNumberOfElements()).isEqualTo(vBooksListSize);
        assertThat(result.iterator().next()).isInstanceOf(Book.class);
        assertThat(result.getSize()).isEqualTo(vMaxBooksPerPage);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result).usingRecursiveFieldByFieldElementComparator().isEqualTo(vBookPage);
    }

}
