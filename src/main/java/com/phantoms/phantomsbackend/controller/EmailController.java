package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.common.bean.ResultInfo;
import com.phantoms.phantomsbackend.common.utils.EmailUtil;
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
import java.util.List;
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
        emailUtil.sendSimpleEmail(to, "Test Subject", "This is a test email.");
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
}