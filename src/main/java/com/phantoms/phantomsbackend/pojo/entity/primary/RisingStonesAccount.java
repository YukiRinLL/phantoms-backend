package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "RisingStonesAccount")
@Table(name = "rising_stones_account")
@Data
public class RisingStonesAccount {

    @Id
    @Column(name = "account_id", length = 32)
    private String accountId;

    @Column(name = "cookies", nullable = false, columnDefinition = "TEXT")
    private String cookies;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "default_for_api", nullable = false)
    private Boolean defaultForApi = false;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "character_name", length = 100)
    private String characterName;

    @Column(name = "server_name", length = 50)
    private String serverName;

    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "experience", length = 50)
    private String experience;

    @Column(name = "last_sign_in_time")
    private Long lastSignInTime;

    @Column(name = "last_sign_in_result", length = 200)
    private String lastSignInResult;

    @Column(name = "user_info_update_time")
    private Long userInfoUpdateTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
