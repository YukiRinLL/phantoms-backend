package com.phantoms.phantomsbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDTO {
    private UUID id;
    private UUID legacyUserId;
    private UUID userId;
    private String message;
    private LocalDateTime createdAt;
}