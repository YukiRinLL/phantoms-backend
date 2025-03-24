package com.phantoms.phantomsbackend.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", schema = "auth")
@Data
public class AuthUser {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "aud")
    private String aud;

    @Column(name = "role")
    private String role;

    @Column(name = "email")
    private String email;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @Column(name = "email_confirmed_at")
    private LocalDateTime emailConfirmedAt;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "confirmation_sent_at")
    private LocalDateTime confirmationSentAt;

    @Column(name = "recovery_token")
    private String recoveryToken;

    @Column(name = "recovery_sent_at")
    private LocalDateTime recoverySentAt;

    @Column(name = "email_change_token_new")
    private String emailChangeTokenNew;

    @Column(name = "email_change")
    private String emailChange;

    @Column(name = "email_change_sent_at")
    private LocalDateTime emailChangeSentAt;

    @Column(name = "last_sign_in_at")
    private LocalDateTime lastSignInAt;

    @Column(name = "raw_app_meta_data")
    private String rawAppMetaData;

    @Column(name = "raw_user_meta_data")
    private String rawUserMetaData;

    @Column(name = "is_super_admin")
    private Boolean isSuperAdmin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "phone")
    private String phone;

    @Column(name = "phone_confirmed_at")
    private LocalDateTime phoneConfirmedAt;

    @Column(name = "phone_change")
    private String phoneChange;

    @Column(name = "phone_change_token")
    private String phoneChangeToken;

    @Column(name = "phone_change_sent_at")
    private LocalDateTime phoneChangeSentAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "email_change_token_current")
    private String emailChangeTokenCurrent;

    @Column(name = "email_change_confirm_status")
    private String emailChangeConfirmStatus;

    @Column(name = "banned_until")
    private LocalDateTime bannedUntil;

    @Column(name = "reauthentication_token")
    private String reauthenticationToken;

    @Column(name = "reauthentication_sent_at")
    private LocalDateTime reauthenticationSentAt;

    @Column(name = "is_sso_user")
    private boolean isSsoUser;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_anonymous")
    private boolean isAnonymous;

}