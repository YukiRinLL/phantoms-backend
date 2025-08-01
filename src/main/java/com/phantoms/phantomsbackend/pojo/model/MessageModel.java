package com.phantoms.phantomsbackend.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("messages")
public class MessageModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("legacy_user_id")
    private UUID legacyUserId;

    @TableField("user_id")
    private UUID userId;

    @TableField("message")
    private String message;

    @TableField("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}