package com.phantoms.phantomsbackend.pojo.entity.secondary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "SecondaryImage")
@Table(name = "images", schema = "phantoms_db")
@Data
public class Image {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String data;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;
}