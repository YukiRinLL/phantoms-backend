package com.phantoms.phantomsbackend.common.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ApiResponse<T> {
    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("schema")
    private String schema;

    @JsonProperty("rows")
    private List<Map<String, Object>> rows;

    @JsonProperty("row_id")
    private int rowId;

    @JsonProperty("fields")
    private Map<String, Object> fields;

    @JsonProperty("versions")
    private List<Map<String, List<String>>> versions;

    @JsonProperty("sheets")
    private List<Map<String, String>> sheets;
}