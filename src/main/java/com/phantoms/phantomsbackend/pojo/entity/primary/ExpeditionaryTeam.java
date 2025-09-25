package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity(name = "PrimaryExpeditionaryTeam")
@Table(name = "expeditionary_team")
public class ExpeditionaryTeam {
    @Id
    @GeneratedValue
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(name = "free_start_time")
    private LocalTime freeStartTime;

    @Column(name = "free_end_time")
    private LocalTime freeEndTime;

    private String occupation;

    private String notes;

    @Column(name = "volunteer_dungeon")
    private String volunteerDungeon;

    private Integer level;

    @Column(name = "guild_name")
    private String guildName = "Phantom";

    @Column(name = "online_status")
    private Boolean onlineStatus = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}