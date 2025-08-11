package com.phantoms.phantomsbackend.pojo.entity.secondary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "SecondaryUserProfile")
@Table(name = "user_profile", schema = "phantoms_db")
@Data
public class UserProfile {

    @Id
    private String id;

    @Column(name = "legacy_user_id")
    private String legacyUserId;

    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String data;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;
}