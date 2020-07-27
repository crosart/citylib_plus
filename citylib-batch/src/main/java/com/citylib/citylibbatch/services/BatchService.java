package com.citylib.citylibbatch.services;

import com.citylib.citylibbatch.beans.LoanBean;
import com.citylib.citylibbatch.beans.ReservationBean;
import com.citylib.citylibbatch.proxies.CitylibServicesProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
     * A "Batch Run" log is generated each time the job executes through the Scheduled cron for logging reasons.
     * </p>
     */
    @Scheduled(cron = "0 0-59 0-23 * * ?")
    public void mailUnreturnedLoans() {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Unreturned loans batch run");

        List<LoanBean> dueLoans = servicesProxy.getCurrentDueLoans();
        servicesProxy.sendMailForDueLoans(dueLoans);

    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteUnclaimedReservations() {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Unclaimed reservations batch run");

        List<ReservationBean> unclaimedReservations = servicesProxy.getNotifiedReservations();
        for (ReservationBean reservation : unclaimedReservations) {
            if (reservation.getNotificationDate().isAfter(LocalDate.now().plusDays(2))) {
                servicesProxy.deleteReservation(reservation.getId());
            }
        }
    }

}
