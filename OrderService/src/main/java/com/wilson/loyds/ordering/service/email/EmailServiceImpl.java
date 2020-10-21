package com.wilson.loyds.ordering.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * An Email service
 */
@Component
public class EmailServiceImpl implements IEmailService {

    private JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(final JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendEmail(final String recipient, final String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@Loyds.com");
        message.setTo(recipient);
        message.setSubject("New Order");
        message.setText(messageBody);
        emailSender.send(message);
    }
}
