package com.phantoms.phantomsbackend.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageWithUserDTO {
    private UUID messageId;
    private UUID userId;
    private String username;
    private String email;
    private String avatar;
    private String message;
    private LocalDateTime createdAt;
}