package com.wilson.loyds.ordering.service.email;

public interface IEmailService {
    void sendEmail(String recipient, String messageBody);
}
