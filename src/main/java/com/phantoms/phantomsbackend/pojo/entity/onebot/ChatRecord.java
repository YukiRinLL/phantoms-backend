package com.phantoms.phantomsbackend.pojo.entity.onebot;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "chat_records", schema = "onebot")
@TableName(value = "chat_records", schema = "onebot") // MyBatis Plus
public class ChatRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @TableId(type = IdType.ASSIGN_UUID) // MyBatis Plus
    private UUID id;

    @Column(name = "message_type")
    @TableField("message_type")
    private String messageType;

    @Column(name = "qq_user_id")
    @TableField("qq_user_id")
    private Long qqUserId;

    @Column(name = "qq_group_id")
    @TableField("qq_group_id")
    private Long qqGroupId;

    @Column(name = "message")
    @TableField("message")
    private String message;

    @Column(name = "timestamp")
    @TableField("timestamp")
    private LocalDateTime timestamp;

    @Column(name = "created_at")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}