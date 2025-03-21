package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(UUID id, UserDTO userDTO);
    void deleteUser(UUID id);
    void deleteUsers(List<UUID> ids);
}