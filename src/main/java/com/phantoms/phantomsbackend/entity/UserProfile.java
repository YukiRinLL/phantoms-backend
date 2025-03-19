package com.phantoms.phantomsbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "legacy_user_id")
    private UUID legacyUserId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String data;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;
}