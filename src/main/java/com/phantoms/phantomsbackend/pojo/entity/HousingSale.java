package com.phantoms.phantomsbackend.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class HousingSale {
    @JsonProperty("Server")
    private String server;

    @JsonProperty("Area")
    private Integer area;

    @JsonProperty("Slot")
    private Integer slot;

    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("Price")
    private Long price;

    @JsonProperty("Size")
    private Integer size;

    @JsonProperty("FirstSeen")
    private OffsetDateTime firstSeen;

    @JsonProperty("LastSeen")
    private OffsetDateTime lastSeen;

    @JsonProperty("PurchaseType")
    private Integer purchaseType;

    @JsonProperty("RegionType")
    private Integer regionType;

    @JsonProperty("State")
    private Integer state;

    @JsonProperty("Participate")
    private Integer participate;

    @JsonProperty("Winner")
    private String winner;

    @JsonProperty("EndTime")
    private OffsetDateTime endTime;

    @JsonProperty("UpdateTime")
    private OffsetDateTime updateTime;

    /**
     * 获取尺寸名称
     */
    public String getSizeName() {
        switch (size) {
            case 0: return "S";
            case 1: return "M";
            case 2: return "L";
            default: return "未知";
        }
    }

    /**
     * 检查是否为M或L尺寸
     */
    public boolean isMediumOrLarge() {
        return size != null && (size == 1 || size == 2);
    }
}