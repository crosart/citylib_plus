package com.citylib.citylibservices.sources;

import com.citylib.citylibservices.dto.UserDto;
import com.citylib.citylibservices.model.Book;
import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.model.Role;
import com.citylib.citylibservices.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ObjectBuilder {

    public User user() {
        User builtUser = new User();
        builtUser.setId(1);
        builtUser.setEmail("email@mail.com");
        builtUser.setPassword("password");
        builtUser.setUsername("username");

        return builtUser;
    }

    public Role role() {
        Role builtRole = new Role();
        builtRole.setId(1);
        builtRole.setName("nom");
        builtRole.setDef(true);

        return builtRole;
    }

    public UserDto userDto() {
        UserDto builtUserDto = new UserDto();
        builtUserDto.setEmail("email@mail.com");
        builtUserDto.setPassword("password");
        builtUserDto.setMatchingPassword("password");
        builtUserDto.setUsername("username");

        return builtUserDto;
    }

    public Book book() {
        Book builtBook = new Book();
        builtBook.setId(1);
        builtBook.setIsbn("1234567890123");
        builtBook.setTitle("titre");
        builtBook.setAuthor("auteur");
        builtBook.setEditor("editeur");
        builtBook.setYear(2020);
        builtBook.setSummary("summary");
        builtBook.setQuantity(5);
        builtBook.setImage("imageurl");
        builtBook.setGenre("genre");
        return builtBook;
    }

    public Loan loan() {
        Loan builtLoan = new Loan();
        builtLoan.setId(1);
        builtLoan.setDue(LocalDate.now().plusWeeks(2).plusDays(1));
        builtLoan.setReturned(false);
        builtLoan.setExtended(false);
        builtLoan.setBook(this.book());
        builtLoan.setUser(this.user());
        return builtLoan;
    }

    public List<Book> booksList(int listSize) {
        IntFunction<Book> bookBuilder = i -> {
            Book builtBook = new Book();
            builtBook.setId(i);
            builtBook.setIsbn(String.format("%010d", (i)));
            builtBook.setTitle("titre" + (i));
            builtBook.setAuthor("auteur" + (i));
            builtBook.setEditor("editeur" + (i));
            builtBook.setYear(2020 - i);
            builtBook.setSummary("summary" + (i));
            builtBook.setQuantity(i);
            builtBook.setImage("imageurl" + (i));
            builtBook.setGenre("genre" + (i));
            return builtBook;
        };
        return IntStream.rangeClosed(1, listSize).mapToObj(bookBuilder).collect(Collectors.toList());
    }

    public List<Loan> loansList(int listSize) {
        IntFunction<Loan> loanBuilder = i -> {
            Loan builtLoan = new Loan();
            builtLoan.setId(i);
            builtLoan.setDue(LocalDate.now().plusWeeks(2).plusDays(i));
            builtLoan.setReturned(false);
            builtLoan.setExtended(false);
            builtLoan.setBook(this.book());
            builtLoan.setUser(this.user());
            return builtLoan;
        };
        return IntStream.rangeClosed(1, listSize).mapToObj(loanBuilder).collect(Collectors.toList());
    }

    public List<Loan> dueLoansList(int listSize) {
        IntFunction<Loan> loanBuilder = i -> {
            Loan builtLoan = new Loan();
            builtLoan.setId(i);
            builtLoan.setDue(LocalDate.now().minusWeeks(2).minusDays(i));
            builtLoan.setReturned(false);
            builtLoan.setExtended(false);
            builtLoan.setBook(this.book());
            builtLoan.setUser(this.user());
            return builtLoan;
        };
        return IntStream.rangeClosed(1, listSize).mapToObj(loanBuilder).collect(Collectors.toList());
    }
}
