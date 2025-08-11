package com.phantoms.phantomsbackend.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("messages")
public class MessageModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("legacy_user_id")
    private String legacyUserId;

    @TableField("user_id")
    private String userId;

    @TableField("message")
    private String message;

    @TableField("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}