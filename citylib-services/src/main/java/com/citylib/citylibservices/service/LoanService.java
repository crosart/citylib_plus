package com.citylib.citylibservices.service;

import com.citylib.citylibservices.config.SmtpConfig;
import com.citylib.citylibservices.model.Reservation;
import com.citylib.citylibservices.repository.ReservationRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Service
public class LoanService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private SmtpConfig smtpConfig;

    public void sendMailToFirstReservation(long bookId) {
        List<Reservation> listReservations = reservationRepository.findByBook_IdOrderByIdAsc(bookId);
        String mailFrom = smtpConfig.getSmtpUser();
        String mailSubject = "CityLib : Un livre que vous avez réservé est disponible !";
        String mailText = listReservations.get(0).getBook().getTitle() + "est disponible ! Venez le récupérer à la bibliothèque sous 48H !";

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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(listReservations.get(0).getUser().getEmail()));
            message.setSubject(mailSubject);
            message.setText(mailText);

            Transport.send(message);
            System.out.println("Email sent !");

        } catch (MessagingException e) {
            e.printStackTrace();
            LoggerFactory.getLogger(this.getClass()).info(e.getMessage());
        }
    }

}
