package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.pojo.entity.AuthUser;
import com.phantoms.phantomsbackend.repository.primary.PrimaryAuthUserRepository;
import com.phantoms.phantomsbackend.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    @Qualifier("primaryAuthUserRepository")
    private PrimaryAuthUserRepository authUserRepository;

    @Override
    public List<AuthUser> getAllUsers() {
        return authUserRepository.findAll();
    }

    @Override
    public Optional<AuthUser> getUserByEmail(String email) {
        return authUserRepository.findByEmail(email);
    }
}