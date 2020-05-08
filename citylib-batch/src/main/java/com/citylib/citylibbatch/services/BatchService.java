package com.citylib.citylibbatch.services;

import com.citylib.citylibbatch.beans.BookBean;
import com.citylib.citylibbatch.beans.LoanBean;
import com.citylib.citylibbatch.beans.UserBean;
import com.citylib.citylibbatch.config.SmtpConfig;
import com.citylib.citylibbatch.proxies.CitylibServicesProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Service
public class BatchService {

    @Autowired
    CitylibServicesProxy servicesProxy;

    @Scheduled(cron = "0 0-59 0-23 * * ?")
    public void mailUnreturnedLoans() {

        List<LoanBean> dueLoans = servicesProxy.getCurrentDueLoans();
        for (int i = 0; i < dueLoans.size(); i++) {
            UserBean user = servicesProxy.getUserById(dueLoans.get(i).getUserId());
            BookBean book = servicesProxy.getBookById(dueLoans.get(i).getBookId());
            this.sendMail(user, book);
        }

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("BEEP");
    }

    @Autowired
    SmtpConfig smtpConfig;

    private void sendMail(UserBean user, BookBean book) {

        String mailFrom = smtpConfig.getSmtpUser();
        String mailSubject = "Citylib : Rappel pour votre emprunt de livre";
        String mailText = "Livre à ramener : ";

        Properties prop = System.getProperties();

        prop.setProperty("mail.smtp.host", smtpConfig.getSmtpHost());
        prop.setProperty("mail.smtp.port", smtpConfig.getSmtpPort());
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");

        Session session = Session.getDefaultInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpConfig.getSmtpUser(), smtpConfig.getSmtpPass());
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject(mailSubject);
            message.setText(mailText + book.getTitle());

            Transport.send(message);
            System.out.println("Email sent !");

        } catch (MessagingException e) {
            e.printStackTrace();
            LoggerFactory.getLogger(this.getClass()).info(e.getMessage());
        }

    }

}
