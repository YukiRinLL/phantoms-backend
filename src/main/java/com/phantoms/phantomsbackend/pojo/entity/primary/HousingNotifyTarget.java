package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "HousingNotifyTarget")
@Table(name = "housing_notify_target", schema = "config")
@Data
public class HousingNotifyTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HousingNotifyServer> servers = new ArrayList<>();

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HousingNotifyArea> areas = new ArrayList<>();

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HousingNotifyGroup> groups = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
    }

    public void addServer(String serverId) {
        HousingNotifyServer server = new HousingNotifyServer();
        server.setTarget(this);
        server.setServerId(serverId);
        servers.add(server);
    }

    public void addArea(Integer areaId) {
        HousingNotifyArea area = new HousingNotifyArea();
        area.setTarget(this);
        area.setAreaId(areaId);
        areas.add(area);
    }

    public void addGroup(String groupId) {
        HousingNotifyGroup group = new HousingNotifyGroup();
        group.setTarget(this);
        group.setGroupId(groupId);
        groups.add(group);
    }
}
