package com.phantoms.phantomsbackend.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import lombok.Data;
import java.util.List;

@Data
public class RecruitmentResponse {
    private List<Recruitment> data;
    private Pagination pagination;
}

@Data
class Pagination {
    @JsonProperty("total")
    private int total;
    @JsonProperty("page")
    private int page;
    @JsonProperty("per_page")
    private int perPage;
    @JsonProperty("total_pages")
    private int totalPages;
}