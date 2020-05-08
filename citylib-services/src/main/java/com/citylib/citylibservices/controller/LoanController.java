package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @GetMapping(value = "/dueloans")
    public List<Loan> getCurrentDueLoans() {
        return loanRepository.findByDueLessThanEqualAndReturnedFalse(LocalDate.now());
    }
}
