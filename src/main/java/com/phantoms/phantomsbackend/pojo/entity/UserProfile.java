package com.phantoms.phantomsbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_profile")
@TableName("user_profile") // MyBatis Plus
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @TableId(type = IdType.ASSIGN_UUID) // MyBatis Plus
    private UUID id;

    @Column(name = "legacy_user_id")
    @TableField("legacy_user_id")
    private UUID legacyUserId;

    @Column(name = "user_id")
    @TableField("user_id")
    private UUID userId;

    @Column(nullable = false)
    @TableField("name")
    private String name;

    @Column(nullable = false)
    @TableField("data")
    private String data;

    @Column(name = "created_at")
    @TableField("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "uploaded_by")
    @TableField("uploaded_by")
    private String uploadedBy;
}