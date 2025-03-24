package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.common.bean.ResultInfo;
import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.pojo.dto.NewUserRequestDTO;
import com.phantoms.phantomsbackend.pojo.dto.UserDTO;
import com.phantoms.phantomsbackend.service.EmailService;
import com.phantoms.phantomsbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Email Controller", description = "Email operations")
public class EmailController {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-test-email")
    public ResponseEntity<String> sendEmail(@RequestParam String to) {
        String subject = "Test Subject";
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", subject);
        templateVariables.put("recipientName", to);
        templateVariables.put("messageBody", "这是一封测试邮件，用于展示 HTML 邮件模板的效果。你可以根据需要修改内容和样式。");
        templateVariables.put("footerText", "感谢你的支持！");
        templateVariables.put("buttonText", "buttonText");
        templateVariables.put("buttonLink", "buttonLink");
        templateVariables.put("footerCopyright", "版权所有 © 2025");

        emailUtil.sendDefaultHtmlEmail(to, subject, templateVariables);
        emailUtil.sendSimpleEmail(to, subject, "This is a test email.");
        return ResponseEntity.ok("Test email sent");
    }

    @PostMapping("/send-email-to-all")
    @Operation(
            summary = "Send email to all users",
            description = "Send an email to all registered users with the specified subject and text"
    )
    public ResultInfo<String> sendEmailToAllUsers(@RequestBody EmailContent emailContent) {
        emailService.sendEmailToAllUsers(emailContent.getSubject(), emailContent.getText());
        return ResultInfo.ok("Email sent to all users");
    }

    @Data
    private static class EmailContent {
        private String subject;
        private String text;
    }

    @PostMapping("/new-user")
    public String newUser(@RequestBody NewUserRequestDTO newUserRequest) {
        sendWelcomeEmail(newUserRequest);
        System.out.println("New user registered: " + newUserRequest.getEmail());
        return "Welcome email sent successfully";
    }
    private void sendWelcomeEmail(NewUserRequestDTO newUserRequest) {
        String subject = "Welcome to Our Service!";
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", subject);
        templateVariables.put("recipientName", newUserRequest.getEmail());//暂时显示Email作为用户名
        templateVariables.put("instance_id", newUserRequest.getInstance_id());
        templateVariables.put("id", newUserRequest.getId());
        templateVariables.put("aud", newUserRequest.getAud());
        templateVariables.put("role", newUserRequest.getRole());
        templateVariables.put("email", newUserRequest.getEmail());
        templateVariables.put("encrypted_password", newUserRequest.getEncrypted_password());
        templateVariables.put("email_confirmed_at", newUserRequest.getEmail_confirmed_at());
        templateVariables.put("invited_at", newUserRequest.getInvited_at());
        templateVariables.put("confirmation_token", newUserRequest.getConfirmation_token());
        templateVariables.put("confirmation_sent_at", newUserRequest.getConfirmation_sent_at());
        templateVariables.put("recovery_token", newUserRequest.getRecovery_token());
        templateVariables.put("recovery_sent_at", newUserRequest.getRecovery_sent_at());
        templateVariables.put("email_change_token_new", newUserRequest.getEmail_change_token_new());
        templateVariables.put("email_change", newUserRequest.getEmail_change());
        templateVariables.put("email_change_sent_at", newUserRequest.getEmail_change_sent_at());
        templateVariables.put("last_sign_in_at", newUserRequest.getLast_sign_in_at());
        templateVariables.put("raw_app_meta_data", newUserRequest.getRaw_app_meta_data());
        templateVariables.put("raw_user_meta_data", newUserRequest.getRaw_user_meta_data());
        templateVariables.put("is_super_admin", newUserRequest.is_super_admin());
        templateVariables.put("created_at", newUserRequest.getCreated_at());
        templateVariables.put("updated_at", newUserRequest.getUpdated_at());
        templateVariables.put("phone", newUserRequest.getPhone());
        templateVariables.put("phone_confirmed_at", newUserRequest.getPhone_confirmed_at());
        templateVariables.put("phone_change", newUserRequest.getPhone_change());
        templateVariables.put("phone_change_token", newUserRequest.getPhone_change_token());
        templateVariables.put("phone_change_sent_at", newUserRequest.getPhone_change_sent_at());
        templateVariables.put("confirmed_at", newUserRequest.getConfirmed_at());
        templateVariables.put("email_change_token_current", newUserRequest.getEmail_change_token_current());
        templateVariables.put("email_change_confirm_status", newUserRequest.getEmail_change_confirm_status());
        templateVariables.put("banned_until", newUserRequest.getBanned_until());
        templateVariables.put("reauthentication_token", newUserRequest.getReauthentication_token());
        templateVariables.put("reauthentication_sent_at", newUserRequest.getReauthentication_sent_at());
        templateVariables.put("is_sso_user", newUserRequest.is_sso_user());
        templateVariables.put("deleted_at", newUserRequest.getDeleted_at());
        templateVariables.put("is_anonymous", newUserRequest.is_anonymous());
        templateVariables.put("messageBody", "Welcome to our service! We are excited to have you on board.");
        templateVariables.put("footerText", "Thank you for joining us!");
        templateVariables.put("buttonText", "Get Started");
        templateVariables.put("buttonLink", "https://example.com");
        templateVariables.put("footerCopyright", "版权所有 © 2025");

        emailUtil.sendWelcomeHtmlEmail(newUserRequest.getEmail(), subject, templateVariables);
    }
}