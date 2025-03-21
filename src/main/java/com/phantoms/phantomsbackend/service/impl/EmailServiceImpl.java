package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailUtil emailUtil;

    @Override
    public void sendWelcomeEmail(String email) {
        String subject = "Welcome to Our Service";
        String text = "Thank you for registering with us!";
        emailUtil.sendSimpleEmail(email, subject, text);
    }
}