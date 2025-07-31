package com.phantoms.phantomsbackend.pojo.entity.onebot;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

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

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getUserId() {
        return qqUserId;
    }

    public void setUserId(Long userId) {
        this.qqUserId = userId;
    }

    public Long getGroupId() {
        return qqGroupId;
    }

    public void setGroupId(Long groupId) {
        this.qqGroupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}