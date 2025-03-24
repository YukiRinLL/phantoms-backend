package com.phantoms.phantomsbackend.service;


public interface EmailService {

    void sendAuthUserDetailEmail(String email);

    void sendEmailToAllUsers(String subject, String text);
}