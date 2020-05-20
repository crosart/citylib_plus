package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @GetMapping("/due")
    public List<Loan> getCurrentDueLoans() {
        return loanRepository.findByDueLessThanEqualAndReturnedFalse(LocalDate.now());
    }

    @GetMapping("/user/{id}")
    public Page<Loan> getUserLoansByUserId(@PathVariable long id) {
        return loanRepository.findByUserId(id);
    }

    @GetMapping("/extend/{id}")
    public ResponseEntity<Loan> extendLoan(@PathVariable("id") long id) {
        Optional<Loan> loanData = loanRepository.findById(id);
        if (loanData.isPresent()) {
            Loan _loan = loanData.get();
            _loan.setDue(loanData.get().getDue().plusWeeks(4));
            _loan.setExtended(true);
            return new ResponseEntity<>(loanRepository.save(_loan), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping("loans/user/{id}")
//    public List<Loan> getUserLoans(@PathVariable "id" long id)

}
