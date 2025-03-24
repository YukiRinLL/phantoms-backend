package com.phantoms.phantomsbackend.pojo.dto;

import lombok.Data;

@Data
public class NewUserRequestDTO {
    private String instance_id;
    private String id;
    private String aud;
    private String role;
    private String email;
    private String encrypted_password;
    private String email_confirmed_at;
    private String invited_at;
    private String confirmation_token;
    private String confirmation_sent_at;
    private String recovery_token;
    private String recovery_sent_at;
    private String email_change_token_new;
    private String email_change;
    private String email_change_sent_at;
    private String last_sign_in_at;
    private String raw_app_meta_data;
    private String raw_user_meta_data;
    private boolean is_super_admin;
    private String created_at;
    private String updated_at;
    private String phone;
    private String phone_confirmed_at;
    private String phone_change;
    private String phone_change_token;
    private String phone_change_sent_at;
    private String confirmed_at;
    private String email_change_token_current;
    private String email_change_confirm_status;
    private String banned_until;
    private String reauthentication_token;
    private String reauthentication_sent_at;
    private boolean is_sso_user;
    private String deleted_at;
    private boolean is_anonymous;
}