package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.pojo.dto.MessageWithUserDTO;
import com.phantoms.phantomsbackend.pojo.dto.UserWithAvatarDTO;
import com.phantoms.phantomsbackend.pojo.entity.Message;
import com.phantoms.phantomsbackend.pojo.entity.User;
import com.phantoms.phantomsbackend.repository.MessageRepository;
import com.phantoms.phantomsbackend.repository.UserRepository;
import com.phantoms.phantomsbackend.service.MessageService;
import com.phantoms.phantomsbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<MessageWithUserDTO> getAllMessagesWithUserDetails() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream()
                .map(message -> {
                    User user = userRepository.findById(message.getLegacyUserId()).orElse(null);
                    if (user != null) {
                        MessageWithUserDTO messageWithUserDTO = new MessageWithUserDTO();
                        messageWithUserDTO.setMessageId(message.getId());
                        messageWithUserDTO.setUserId(user.getId());
                        messageWithUserDTO.setUsername(user.getUsername());
                        messageWithUserDTO.setEmail(user.getEmail());
                        messageWithUserDTO.setMessage(message.getMessage());
                        messageWithUserDTO.setCreatedAt(message.getCreatedAt());
                        UserWithAvatarDTO userWithAvatarDTO = userService.getUserWithAvatarByLegacyUserId(user.getId());
                        if (userWithAvatarDTO != null) {
                            messageWithUserDTO.setAvatar(userWithAvatarDTO.getAvatar());
                        }
                        return messageWithUserDTO;
                    }
                    return null;
                })
                .filter(messageWithUserDTO -> messageWithUserDTO != null)
                .collect(Collectors.toList());
    }
}