package com.phantoms.phantomsbackend.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "legacy_user_id")
    private UUID legacyUserId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}