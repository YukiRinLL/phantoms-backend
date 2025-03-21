package com.phantoms.phantomsbackend.service;


public interface EmailService {

    void sendWelcomeEmail(String email);

    void sendEmailToAllUsers(String subject, String text);
}