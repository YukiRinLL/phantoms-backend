package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity(name = "PrimaryPassword")
@Table(name = "passwords")
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(unique = true, nullable = false, name = "legacy_user_id")
    private UUID legacyUserId;

    @Column(nullable = false)
    private String password;
}