package com.phantoms.phantomsbackend.pojo.entity.secondary;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity(name = "SecondaryExpeditionaryTeam")
@Table(name = "expeditionary_team", schema = "phantoms_db")
@Comment("远征队信息表")
public class ExpeditionaryTeam {

    @Id
    @Column(name = "uuid", length = 36, nullable = false)
    private String uuid;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "free_start_time")
    private LocalTime freeStartTime;

    @Column(name = "free_end_time")
    private LocalTime freeEndTime;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "volunteer_dungeon", length = 200)
    private String volunteerDungeon;

    @Column(name = "level")
    private Integer level;

    @Column(name = "guild_name", length = 100)
    private String guildName = "Phantom";

    @Column(name = "online_status")
    private Boolean onlineStatus = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}