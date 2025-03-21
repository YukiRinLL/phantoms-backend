package com.phantoms.phantomsbackend.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserProfileDTO {
    private UUID id;
    private UUID legacyUserId;
    private UUID userId;
    private String name;
    private String data;
    private LocalDateTime createdAt;
    private String uploadedBy;

}