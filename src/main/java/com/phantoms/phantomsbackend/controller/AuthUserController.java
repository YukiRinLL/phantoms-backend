package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.primary.AuthUser;
import com.phantoms.phantomsbackend.service.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<AuthUser> getAllUsers() {
        return authUserService.getAllUsers();
    }
}
