package com.phantoms.phantomsbackend.pojo.entity.secondary.onebot;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "SecondaryChatRecord")
@Table(name = "chat_records", schema = "phantoms_db")
@Data
public class ChatRecord {

    @Id
    private String id;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "qq_user_id")
    private Long qqUserId;

    @Column(name = "qq_group_id")
    private Long qqGroupId;

    @Column(name = "message")
    private String message;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}