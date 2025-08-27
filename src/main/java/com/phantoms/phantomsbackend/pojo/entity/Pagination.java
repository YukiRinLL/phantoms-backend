package com.phantoms.phantomsbackend.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Pagination {
    @JsonProperty("total")
    private int total;
    @JsonProperty("page")
    private int page;
    @JsonProperty("per_page")
    private int perPage;
    @JsonProperty("total_pages")
    private int totalPages;
}