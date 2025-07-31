package com.phantoms.phantomsbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "users") // JPA
@TableName("users") // MyBatis Plus
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    @TableField("username")
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    @TableField("email")
    private String email;

    @Column(name = "user_id")
    @TableField("user_id")
    private UUID userId;
}