package com.phantoms.phantomsbackend.pojo.entity.primary;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;

@Data
@Entity(name = "Recruitment")
@Table(name = "recruitments")
public class Recruitment {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @Column(name = "created_world")
    @JsonProperty("created_world")
    private String createdWorld;

    @Column(name = "created_world_id")
    @JsonProperty("created_world_id")
    private int createdWorldId;

    @Column(name = "home_world")
    @JsonProperty("home_world")
    private String homeWorld;

    @Column(name = "home_world_id")
    @JsonProperty("home_world_id")
    private int homeWorldId;

    @Column(name = "category")
    @JsonProperty("category")
    private String category;

    @Column(name = "category_id")
    @JsonProperty("category_id")
    private int categoryId;

    @Column(name = "duty")
    @JsonProperty("duty")
    private String duty;

    @Column(name = "min_item_level")
    @JsonProperty("min_item_level")
    private int minItemLevel;

    @Column(name = "slots_filled")
    @JsonProperty("slots_filled")
    private int slotsFilled;

    @Column(name = "slots_available")
    @JsonProperty("slots_available")
    private int slotsAvailable;

    @Column(name = "time_left")
    @JsonProperty("time_left")
    private double timeLeft;

    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime updatedAt;

    @Column(name = "is_cross_world")
    @JsonProperty("is_cross_world")
    private boolean isCrossWorld;

    @Column(name = "datacenter")
    @JsonProperty("datacenter")
    private String datacenter;
}