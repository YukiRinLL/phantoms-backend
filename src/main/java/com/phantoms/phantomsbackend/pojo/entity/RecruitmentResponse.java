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