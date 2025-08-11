package com.phantoms.phantomsbackend.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfileModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("legacy_user_id")
    private String legacyUserId;

    @TableField("user_id")
    private String userId;

    @TableField("name")
    private String name;

    @TableField("data")
    private String data;

    @TableField("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @TableField("uploaded_by")
    private String uploadedBy;
}