package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.pojo.dto.UserDTO;
import com.phantoms.phantomsbackend.pojo.dto.UserWithAvatarDTO;
import com.phantoms.phantomsbackend.pojo.entity.User;
import com.phantoms.phantomsbackend.pojo.entity.UserProfile;
import com.phantoms.phantomsbackend.repository.UserProfileRepository;
import com.phantoms.phantomsbackend.repository.UserRepository;
import com.phantoms.phantomsbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        // 确保设置密码
        // user.setPassword(userDTO.getPassword());
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        // 确保更新密码
        // user.setPassword(userDTO.getPassword());
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteUsers(List<UUID> ids) {
        ids.forEach(this::deleteUser);
    }

    @Override
    public UserDTO findByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return convertToDTO(userOptional.get());
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }

    @Override
    public UserWithAvatarDTO getUserWithAvatarByUserId(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElse(null);
        return convertToUserWithAvatarDTO(user, userProfile);
    }

    @Override
    public UserWithAvatarDTO getUserWithAvatarByLegacyUserId(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile userProfile = userProfileRepository.findByLegacyUserId(userId).orElse(null);
        return convertToUserWithAvatarDTO(user, userProfile);
    }

    @Override
    public List<UserWithAvatarDTO> getAllUsersWithAvatar() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserProfile userProfile = userProfileRepository.findByUserId(user.getUserId()).orElse(null);
                    return convertToUserWithAvatarDTO(user, userProfile);
                })
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    private UserWithAvatarDTO convertToUserWithAvatarDTO(User user, UserProfile userProfile) {
        UserWithAvatarDTO userWithAvatarDTO = new UserWithAvatarDTO();
        BeanUtils.copyProperties(user, userWithAvatarDTO);
        if (userProfile != null) {
            userWithAvatarDTO.setAvatar(userProfile.getData());
        }
        return userWithAvatarDTO;
    }
}