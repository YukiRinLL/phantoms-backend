package com.phantoms.phantomsbackend.pojo.model.onebot;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName(value = "chat_records")
public class ChatRecordModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("message_type")
    private String messageType;

    @TableField("qq_user_id")
    private Long qqUserId;

    @TableField("qq_group_id")
    private Long qqGroupId;

    @TableField("message")
    private String message;

    @TableField("timestamp")
    private LocalDateTime timestamp;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}