package com.phantoms.phantomsbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users", schema = "auth")
@TableName(value = "users", schema = "auth") // MyBatis Plus
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @TableId(type = IdType.ASSIGN_UUID) // MyBatis Plus
    private UUID id;

    @Column(name = "instance_id", columnDefinition = "UUID")
    @TableField("instance_id")
    private UUID instanceId;

    @Column(name = "aud")
    @TableField("aud")
    private String aud;

    @Column(name = "role")
    @TableField("role")
    private String role;

    @Column(name = "email")
    @TableField("email")
    private String email;

    @Column(name = "encrypted_password")
    @TableField("encrypted_password")
    private String encryptedPassword;

    @Column(name = "email_confirmed_at")
    @TableField("email_confirmed_at")
    private LocalDateTime emailConfirmedAt;

    @Column(name = "invited_at")
    @TableField("invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "confirmation_token")
    @TableField("confirmation_token")
    private String confirmationToken;

    @Column(name = "confirmation_sent_at")
    @TableField("confirmation_sent_at")
    private LocalDateTime confirmationSentAt;

    @Column(name = "recovery_token")
    @TableField("recovery_token")
    private String recoveryToken;

    @Column(name = "recovery_sent_at")
    @TableField("recovery_sent_at")
    private LocalDateTime recoverySentAt;

    @Column(name = "email_change_token_new")
    @TableField("email_change_token_new")
    private String emailChangeTokenNew;

    @Column(name = "email_change")
    @TableField("email_change")
    private String emailChange;

    @Column(name = "email_change_sent_at")
    @TableField("email_change_sent_at")
    private LocalDateTime emailChangeSentAt;

    @Column(name = "last_sign_in_at")
    @TableField("last_sign_in_at")
    private LocalDateTime lastSignInAt;

    @Column(name = "raw_app_meta_data", columnDefinition = "jsonb")
    @TableField("raw_app_meta_data")
    private String rawAppMetaData;

    @Column(name = "raw_user_meta_data", columnDefinition = "jsonb")
    @TableField("raw_user_meta_data")
    private String rawUserMetaData;

    @Column(name = "is_super_admin")
    @TableField("is_super_admin")
    private Boolean isSuperAdmin;

    @Column(name = "created_at")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "phone")
    @TableField("phone")
    private String phone;

    @Column(name = "phone_confirmed_at")
    @TableField("phone_confirmed_at")
    private LocalDateTime phoneConfirmedAt;

    @Column(name = "phone_change")
    @TableField("phone_change")
    private String phoneChange;

    @Column(name = "phone_change_token")
    @TableField("phone_change_token")
    private String phoneChangeToken;

    @Column(name = "phone_change_sent_at")
    @TableField("phone_change_sent_at")
    private LocalDateTime phoneChangeSentAt;

    @Column(name = "confirmed_at")
    @TableField("confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "email_change_token_current")
    @TableField("email_change_token_current")
    private String emailChangeTokenCurrent;

    @Column(name = "email_change_confirm_status")
    @TableField("email_change_confirm_status")
    private short emailChangeConfirmStatus;

    @Column(name = "banned_until")
    @TableField("banned_until")
    private LocalDateTime bannedUntil;

    @Column(name = "reauthentication_token")
    @TableField("reauthentication_token")
    private String reauthenticationToken;

    @Column(name = "reauthentication_sent_at")
    @TableField("reauthentication_sent_at")
    private LocalDateTime reauthenticationSentAt;

    @Column(name = "is_sso_user")
    @TableField("is_sso_user")
    private boolean isSsoUser;

    @Column(name = "deleted_at")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_anonymous")
    @TableField("is_anonymous")
    private boolean isAnonymous;
}