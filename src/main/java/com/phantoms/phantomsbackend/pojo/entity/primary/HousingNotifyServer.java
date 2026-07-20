package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "HousingNotifyServer")
@Table(name = "housing_notify_server", schema = "config")
@Data
public class HousingNotifyServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private HousingNotifyTarget target;

    @Column(name = "server_id", nullable = false, length = 50)
    private String serverId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
