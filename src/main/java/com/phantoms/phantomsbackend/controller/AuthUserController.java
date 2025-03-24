package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.AuthUser;
import com.phantoms.phantomsbackend.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthUserController {

    @Autowired
    private AuthUserService authUserService;

    @GetMapping("/users")
    public List<AuthUser> getAllUsers() {
        return authUserService.getAllUsers();
    }
}