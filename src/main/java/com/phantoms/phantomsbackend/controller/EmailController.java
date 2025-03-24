package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.common.bean.ResultInfo;
import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.service.EmailService;
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

    @PostMapping("/auth-user-info")
    public String newUser(@RequestBody AuthUserEmailDTO authUserEmailDTO) {
        emailService.sendAuthUserDetailEmail(authUserEmailDTO.getEmail());
        System.out.println("Auth user info sent: " + authUserEmailDTO.getEmail());
        return "Auth user info email sent successfully";
    }
    @Data
    private static class AuthUserEmailDTO{
        private String email;
    }
}