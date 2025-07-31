package com.phantoms.phantomsbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "images")
@TableName("images") // MyBatis Plus表名注解
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @TableId(type = IdType.ASSIGN_UUID) // MyBatis Plus主键注解
    private UUID id;

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