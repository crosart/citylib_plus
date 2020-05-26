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

/**
 * Batch job to generate emails to users having not returned their expired loans.
 *
 * @author crosart
 */

@Service
public class BatchService {

    @Autowired
    CitylibServicesProxy servicesProxy;

    /**
     * Method checking for unreturned expired loans, sending a mail for each retrieved loan.
     * <p>
     * A "BEEP" is generated each time the job executes through the Scheduled cron for logging reasons.
     * </p>
     */
    @Scheduled(cron = "0 0-59 0-23 * * ?")
    public void mailUnreturnedLoans() {

        List<LoanBean> dueLoans = servicesProxy.getCurrentDueLoans();
        for (int i = 0; i < dueLoans.size(); i++) {
            UserBean user = dueLoans.get(i).getUser();
            BookBean book = dueLoans.get(i).getBook();
            this.sendMail(user, book);
        }

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("BEEP");
    }

    @Autowired
    SmtpConfig smtpConfig;

    /**
     * Method building and sending an email with parameters from {@link #mailUnreturnedLoans()}
     *
     * @param user {@link UserBean} attached to the retrieved {@link LoanBean}
     * @param book {@link BookBean} attached to the retrieved {@link LoanBean}
     *
     * @author crosart
     */
    private void sendMail(UserBean user, BookBean book) {

        String mailFrom = smtpConfig.getSmtpUser();
        String mailSubject = "Citylib : Rappel pour votre emprunt de livre";
        String mailText = "Votre prêt de livre est expiré, veuillez nous retourner : ";

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
