package com.phantoms.phantomsbackend.common.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SearchResponse {
    private UUID next;
    private String schema;
    private List<SearchResult> results;
}

