package com.phantoms.phantomsbackend.pojo.entity.secondary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "SecondaryMessage")
@Table(name = "messages", schema = "phantoms_db")
@Data
public class Message {

    @Id
    private String id;

    @Column(name = "legacy_user_id")
    private String legacyUserId;

    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}