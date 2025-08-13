package com.phantoms.phantomsbackend.pojo.entity.secondary;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "SecondaryUser")
@Table(name = "legacy_users", schema = "phantoms_db")
@Data
public class User {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(name = "user_id")
    private String userId;
}