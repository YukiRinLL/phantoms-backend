package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.pojo.dto.UserDTO;
import com.phantoms.phantomsbackend.pojo.entity.primary.AuthUser;
import com.phantoms.phantomsbackend.service.AuthUserService;
import com.phantoms.phantomsbackend.service.EmailService;
import com.phantoms.phantomsbackend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthUserService authUserService;

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

    @Override
    public void sendAuthUserDetailEmail(String email) {
        String subject = "User Information";
        Optional<AuthUser> authUserOptional = authUserService.getUserByEmail(email);
        UserDTO user = userService.findByEmail(email);

        if (authUserOptional.isPresent()) {
            AuthUser authUser = authUserOptional.get();

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("subject", subject);
            templateVariables.put("recipientName", user.getUsername());
            templateVariables.put("instance_id", authUser.getInstanceId());
            templateVariables.put("id", authUser.getId());
            templateVariables.put("aud", authUser.getAud());
            templateVariables.put("role", authUser.getRole());
            templateVariables.put("email", authUser.getEmail());
            templateVariables.put("encrypted_password", authUser.getEncryptedPassword());
            templateVariables.put("email_confirmed_at", authUser.getEmailConfirmedAt());
            templateVariables.put("invited_at", authUser.getInvitedAt());
            templateVariables.put("confirmation_token", authUser.getConfirmationToken());
            templateVariables.put("confirmation_sent_at", authUser.getConfirmationSentAt());
            templateVariables.put("recovery_token", authUser.getRecoveryToken());
            templateVariables.put("recovery_sent_at", authUser.getReauthenticationSentAt());
            templateVariables.put("email_change_token_new", authUser.getEmailChangeTokenNew());
            templateVariables.put("email_change", authUser.getEmailChange());
            templateVariables.put("email_change_sent_at", authUser.getEmailChangeSentAt());
            templateVariables.put("last_sign_in_at", authUser.getLastSignInAt());
            templateVariables.put("raw_app_meta_data", authUser.getRawAppMetaData());
            templateVariables.put("raw_user_meta_data", authUser.getRawUserMetaData());
            templateVariables.put("is_super_admin", authUser.getIsSuperAdmin());
            templateVariables.put("created_at", authUser.getCreatedAt());
            templateVariables.put("updated_at", authUser.getUpdatedAt());
            templateVariables.put("phone", authUser.getPhone());
            templateVariables.put("phone_confirmed_at", authUser.getConfirmedAt());
            templateVariables.put("phone_change", authUser.getPhoneChange());
            templateVariables.put("phone_change_token", authUser.getPhoneChangeToken());
            templateVariables.put("phone_change_sent_at", authUser.getPhoneChangeSentAt());
            templateVariables.put("confirmed_at", authUser.getConfirmedAt());
            templateVariables.put("email_change_token_current", authUser.getEmailChangeTokenCurrent());
            templateVariables.put("email_change_confirm_status", authUser.getEmailChangeConfirmStatus());
            templateVariables.put("banned_until", authUser.getBannedUntil());
            templateVariables.put("reauthentication_token", authUser.getReauthenticationToken());
            templateVariables.put("reauthentication_sent_at", authUser.getReauthenticationSentAt());
            templateVariables.put("is_sso_user", authUser.isSsoUser());
            templateVariables.put("deleted_at", authUser.getDeletedAt());
            templateVariables.put("is_anonymous", authUser.isAnonymous());

//            templateVariables.put("messageBody", "Welcome to our service! We are excited to have you on board.");
            templateVariables.put("footerText", "Thank you for joining us!");
            templateVariables.put("buttonText", "Get Started");
            templateVariables.put("buttonLink", "https://ffxiv-phantoms-mainpage.onrender.com/");
            templateVariables.put("footerCopyright", "版权所有 Phantoms © 2025");

            emailUtil.sendWelcomeHtmlEmail(authUser.getEmail(), subject, templateVariables);
        } else {
            System.out.println("User not found for email: " + email);
        }
    }
}