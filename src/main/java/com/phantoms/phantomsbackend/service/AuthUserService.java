package com.phantoms.phantomsbackend.service;


import com.phantoms.phantomsbackend.pojo.entity.AuthUser;

import java.util.List;
import java.util.Optional;

public interface AuthUserService {
    List<AuthUser> getAllUsers();

    Optional<AuthUser> getUserByEmail(String email);
}