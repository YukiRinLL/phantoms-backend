package com.phantoms.phantomsbackend.pojo.entity.primary.onebot;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data

@Table(name = "user_messages", schema = "onebot")
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "platform")
    private String platform;

    @Column(name = "language")
    private String language;

    @Column(name = "cookies_enabled")
    private Boolean cookiesEnabled;

    @Column(name = "do_not_track")
    private String doNotTrack;

    @Column(name = "java_enabled")
    private Boolean javaEnabled;

    @Column(name = "on_line")
    private Boolean onLine;

    @Column(name = "screen_width")
    private Integer screenWidth;

    @Column(name = "screen_height")
    private Integer screenHeight;

    @Column(name = "color_depth")
    private Integer colorDepth;

    @Column(name = "pixel_ratio")
    private Double pixelRatio;

    @Column(name = "orientation")
    private String orientation;

    @Column(name = "available_memory")
    private Integer availableMemory;

    @Column(name = "hardware_concurrency")
    private Integer hardwareConcurrency;

    @Column(name = "connection_type")
    private String connectionType;

    @Column(name = "downlink")
    private Double downlink;

    @Column(name = "effective_type")
    private String effectiveType;

    @Column(name = "rtt")
    private Double rtt;

    @Column(name = "battery_charging")
    private Boolean batteryCharging;

    @Column(name = "battery_level")
    private Double batteryLevel;

    @Column(name = "battery_charging_time")
    private Double batteryChargingTime;

    @Column(name = "battery_discharging_time")
    private Double batteryDischargingTime;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "plugins")
    private String[] plugins;

    @Column(name = "mime_types")
    private String[] mimeTypes;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "altitude_accuracy")
    private Double altitudeAccuracy;

    @Column(name = "heading")
    private Double heading;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}