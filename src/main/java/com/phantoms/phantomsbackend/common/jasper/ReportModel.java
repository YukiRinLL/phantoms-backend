package com.phantoms.phantomsbackend.common.jasper;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface ReportModel {
    String getTemplate();

    Connection getConnection();

    List<Map<String, Object>> getParameters();

    ContentType getType();

    String getTitle();

    boolean needDatasource();

    public static enum ContentType {
        HTML,
        PDF,
        EXCEL,
        WORD;

        private ContentType() {
        }
    }
}
