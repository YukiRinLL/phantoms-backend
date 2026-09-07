package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.primary.AuthUser;
import com.phantoms.phantomsbackend.service.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth User", description = "认证用户管理接口")
public class AuthUserController {

    @Autowired
    private AuthUserService authUserService;

    @GetMapping("/users")
    @Operation(
            summary = "获取所有认证用户",
            description = "获取系统中所有认证用户的列表",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取用户列表成功")
            }
    )
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(authUserService.getAllUsers().stream().map(user -> {
            java.util.Map<String, Object> safeUser = new java.util.HashMap<>();
            safeUser.put("id", user.getId());
            safeUser.put("email", user.getEmail());
            safeUser.put("emailConfirmedAt", user.getEmailConfirmedAt());
            safeUser.put("createdAt", user.getCreatedAt());
            safeUser.put("lastSignInAt", user.getLastSignInAt());
            return safeUser;
        }).toList());
    }
}
