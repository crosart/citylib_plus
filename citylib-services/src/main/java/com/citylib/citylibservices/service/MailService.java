package com.citylib.citylibservices.service;

import com.citylib.citylibservices.config.SmtpConfig;
import com.citylib.citylibservices.model.Mail;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailService {

    @Autowired
    SmtpConfig smtpConfig;

    public void sendMail(Mail mail) {

        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", smtpConfig.getSmtpHost());
        properties.setProperty("mail.smtp.port", smtpConfig.getSmtpPort());
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpConfig.getSmtpUser(), smtpConfig.getSmtpPass());
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpConfig.getSmtpUser()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getRecipient()));
            message.setSubject(mail.getSubject());
            message.setText(mail.getBody());

            Transport.send(message);
            System.out.println("Email sent !");

        } catch (MessagingException exception) {
            exception.printStackTrace();
            LoggerFactory.getLogger(this.getClass()).info(exception.getMessage());
        }
    }
}
