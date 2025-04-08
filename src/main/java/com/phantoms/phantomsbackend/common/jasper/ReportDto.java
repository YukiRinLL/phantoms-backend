package com.phantoms.phantomsbackend.common.jasper;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReportDto {
    private List<Map<String, Object>> parameters;
    private String reportType;
    private String reportTitle;
    private String templatePath;
}
