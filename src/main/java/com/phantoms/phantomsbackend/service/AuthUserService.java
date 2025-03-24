package com.phantoms.phantomsbackend.service;


import com.phantoms.phantomsbackend.pojo.entity.AuthUser;
import com.phantoms.phantomsbackend.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthUserService {

    @Autowired
    private AuthUserRepository authUserRepository;

    public List<AuthUser> getAllUsers() {
        return authUserRepository.findAll();
    }

    public Optional<AuthUser> getUserByEmail(String email) {
        return authUserRepository.findByEmail(email);
    }
}