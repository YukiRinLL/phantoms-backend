package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.pojo.dto.UserDTO;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Email Controller", description = "")
public class EmailController {

    @Autowired
    private EmailUtil emailUtil;

    @GetMapping("/send-test-email")
    public String sendEmail(@RequestParam String to) {
        emailUtil.sendSimpleEmail(to, "Test Subject", "This is a test email.");
        return "test email sent";
    }
}