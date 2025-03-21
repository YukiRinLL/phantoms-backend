package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.pojo.dto.UserDTO;
import com.phantoms.phantomsbackend.service.EmailService;
import com.phantoms.phantomsbackend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserService userService;

    @Override
    public void sendWelcomeEmail(String email) {
        String subject = "Welcome to Our Service";
        String text = "Thank you for registering with us!";
        emailUtil.sendSimpleEmail(email, subject, text);
    }

    @Override
    @Transactional
    public void sendEmailToAllUsers(String subject, String text) {
        List<UserDTO> users = userService.getAllUsers();
        for (UserDTO user : users) {
            String email = user.getEmail();
            if (email != null && !email.trim().isEmpty()) {
//                emailUtil.sendSimpleEmail(email, subject, text);//TODO 测试，正常需要打开
                System.out.println("Email sent to: " + email);
            } else {
                System.out.println("Skipping user with ID " + user.getId() + " due to missing email address.");
            }
        }
    }
}