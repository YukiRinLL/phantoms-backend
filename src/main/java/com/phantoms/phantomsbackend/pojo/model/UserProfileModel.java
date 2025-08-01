package com.phantoms.phantomsbackend.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("user_profile")
public class UserProfileModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("legacy_user_id")
    private UUID legacyUserId;

    @TableField("user_id")
    private UUID userId;

    @TableField("name")
    private String name;

    @TableField("data")
    private String data;

    @TableField("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @TableField("uploaded_by")
    private String uploadedBy;
}