package com.notification.service;

import com.notification.dto.BookingDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = "email_queue")
    public void receiveMessage(BookingDTO booking) {
        log.info("Received Message for PNR: {}", booking.getPnr());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getEmailId());
        message.setSubject("Flight Confirmed: " + booking.getPnr());
        message.setText("Hello " + booking.getPassengerName() + 
                        ",\nYour flight " + booking.getFlightNumber() + " is confirmed.");

        try {
            mailSender.send(message);
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.error("Error sending email", e);
        }
    }
}
