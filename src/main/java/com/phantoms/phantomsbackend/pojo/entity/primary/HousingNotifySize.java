package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity(name = "HousingNotifySize")
@Table(name = "housing_notify_size", schema = "config")
@Data
@EqualsAndHashCode(exclude = "target")
public class HousingNotifySize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private HousingNotifyTarget target;

    @Column(name = "size_id", nullable = false)
    private Integer sizeId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
