package com.wilson.loyds.ordering.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;
    private EmailServiceImpl classUnderTest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new EmailServiceImpl(emailSender);
    }

    @Test
    void sendEmail() {

        String recipient = "what@gmail.com";
        String messageBody = "You have a new message";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@Loyds.com");
        message.setTo(recipient);
        message.setSubject("New Order");
        message.setText(messageBody);

        classUnderTest.sendEmail(recipient, messageBody);
        verify(this.emailSender).send(message);
        verifyNoMoreInteractions(this.emailSender);
    }

}