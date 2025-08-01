package com.phantoms.phantomsbackend.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName(value = "users", schema = "auth")
public class AuthUserModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("instance_id")
    private UUID instanceId;

    @TableField("aud")
    private String aud;

    @TableField("role")
    private String role;

    @TableField("email")
    private String email;

    @TableField("encrypted_password")
    private String encryptedPassword;

    @TableField("email_confirmed_at")
    private LocalDateTime emailConfirmedAt;

    @TableField("invited_at")
    private LocalDateTime invitedAt;

    @TableField("confirmation_token")
    private String confirmationToken;

    @TableField("confirmation_sent_at")
    private LocalDateTime confirmationSentAt;

    @TableField("recovery_token")
    private String recoveryToken;

    @TableField("recovery_sent_at")
    private LocalDateTime recoverySentAt;

    @TableField("email_change_token_new")
    private String emailChangeTokenNew;

    @TableField("email_change")
    private String emailChange;

    @TableField("email_change_sent_at")
    private LocalDateTime emailChangeSentAt;

    @TableField("last_sign_in_at")
    private LocalDateTime lastSignInAt;

    @TableField("raw_app_meta_data")
    private String rawAppMetaData;

    @TableField("raw_user_meta_data")
    private String rawUserMetaData;

    @TableField("is_super_admin")
    private Boolean isSuperAdmin;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("phone")
    private String phone;

    @TableField("phone_confirmed_at")
    private LocalDateTime phoneConfirmedAt;

    @TableField("phone_change")
    private String phoneChange;

    @TableField("phone_change_token")
    private String phoneChangeToken;

    @TableField("phone_change_sent_at")
    private LocalDateTime phoneChangeSentAt;

    @TableField("confirmed_at")
    private LocalDateTime confirmedAt;

    @TableField("email_change_token_current")
    private String emailChangeTokenCurrent;

    @TableField("email_change_confirm_status")
    private short emailChangeConfirmStatus;

    @TableField("banned_until")
    private LocalDateTime bannedUntil;

    @TableField("reauthentication_token")
    private String reauthenticationToken;

    @TableField("reauthentication_sent_at")
    private LocalDateTime reauthenticationSentAt;

    @TableField("is_sso_user")
    private boolean isSsoUser;

    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    @TableField("is_anonymous")
    private boolean isAnonymous;
}