package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "HousingNotifyGroup")
@Table(name = "housing_notify_group", schema = "config")
@Data
public class HousingNotifyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private HousingNotifyTarget target;

    @Column(name = "group_id", nullable = false, length = 50)
    private String groupId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
