package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.common.bean.ResultInfo;
import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.service.EmailService;
import com.phantoms.phantomsbackend.common.config.AdminApiAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Email Controller", description = "Email operations")
public class EmailController {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AdminApiAccess adminApiAccess;

    @GetMapping("/send-test-email")
    @Operation(
            summary = "Send test email",
            description = "Sends a test email to the specified recipient",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test email sent successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid email address")
            }
    )
    public ResponseEntity<String> sendEmail(@RequestParam String to,
                                            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        if (!adminApiAccess.isAllowed(adminKey)) return ResponseEntity.status(403).build();
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
            description = "Send an email to all registered users with the specified subject and text",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email sent to all users successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failed to send email")
            }
    )
    public ResponseEntity<?> sendEmailToAllUsers(@RequestBody EmailContent emailContent,
                                                 @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        if (!adminApiAccess.isAllowed(adminKey)) return ResponseEntity.status(403).build();
        emailService.sendEmailToAllUsers(emailContent.getSubject(), emailContent.getText());
        return ResponseEntity.ok(ResultInfo.ok("Email sent to all users"));
    }

    @Data
    private static class EmailContent {
        private String subject;
        private String text;
    }

    @PostMapping("/auth-user-info")
    @Operation(
            summary = "Send auth user info email",
            description = "Sends authentication user information email to the specified email address",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Auth user info email sent successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid email address")
            }
    )
    public ResponseEntity<?> newUser(@RequestBody AuthUserEmailDTO authUserEmailDTO,
                                     @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        if (!adminApiAccess.isAllowed(adminKey)) return ResponseEntity.status(403).build();
        emailService.sendAuthUserDetailEmail(authUserEmailDTO.getEmail());
        return ResponseEntity.ok("Auth user info email sent successfully");
    }
    @Data
    private static class AuthUserEmailDTO{
        private String email;
    }
}
