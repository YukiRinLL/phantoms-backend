package com.phantoms.phantomsbackend.pojo.entity.secondary;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "SecondaryPassword")
@Table(name = "passwords", schema = "phantoms_db")
public class Password {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(unique = true, nullable = false, name = "legacy_user_id")
    private String legacyUserId;

    @Column(nullable = false)
    private String password;
}