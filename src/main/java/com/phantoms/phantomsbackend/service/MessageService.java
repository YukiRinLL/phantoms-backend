package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.dto.MessageWithUserDTO;

import java.util.List;

public interface MessageService {
    List<MessageWithUserDTO> getAllMessagesWithUserDetails();
}