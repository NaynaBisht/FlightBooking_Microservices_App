package com.notification.service;

import com.notification.dto.BookingDTO;

import java.util.Map;

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

    @RabbitListener(queues = "otp_email_queue")
    public void receiveOtpMessage(Map<String, String> otpData) {
        String email = otpData.get("email");
        String otp = otpData.get("otp");

        log.info("Received OTP request for email: {}", email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP is: " + otp + "\nThis code will expire shortly.");

        try {
            mailSender.send(message);
            log.info("OTP Email sent successfully to {}", email);
        } catch (Exception e) {
            log.error("Error sending OTP email to {}", email, e);
        }
    }
}
