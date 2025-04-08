package com.phantoms.phantomsbackend.pojo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserWithAvatarDTO {
    private UUID id;
    private String username;
    private String email;
    private String avatar;
}