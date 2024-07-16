package com.wally.service;


import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String userEmail, String link) throws MessagingException;
}
