package com.citylib.citylibservices.controller;

import com.citylib.citylibservices.config.SmtpConfig;
import com.citylib.citylibservices.model.Loan;
import com.citylib.citylibservices.model.Mail;
import com.citylib.citylibservices.service.MailService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping("/due")
    private void sendMailForDueLoans(@RequestBody List<Loan> dueLoans) {

        for (int i = 0; i < dueLoans.size(); i++) {
            Loan currentLoan = dueLoans.get(i);
            Mail mail = new Mail();
            mail.setRecipient(currentLoan.getUser().getEmail());
            mail.setSubject("Citylib : Rappel pour votre emprunt de livre");
            mail.setBody("Votre prêt de livre est expiré, veuillez nous retourner le livre : " + currentLoan.getBook().getTitle());
            mailService.sendMail(mail);
        }

    }

}
