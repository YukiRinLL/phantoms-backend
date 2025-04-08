package com.phantoms.phantomsbackend.common.jasper;

import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JasperModel implements ReportModel{
    private static Log LOGGER = LogFactory.getLog(JasperModel.class);
    private String template;
    private Connection connection;
    private List<Map<String, Object>> parameters;
    private ReportModel.ContentType type;
    private String title;
    private JasperReport report;

    public JasperModel(String template, Connection connection, List<Map<String, Object>> parameters) {
        this.type = ContentType.PDF;
        this.template = template;
        this.setConnection(connection);
        this.parameters = parameters;
    }

    public JasperModel(String template, Connection connection, List<Map<String, Object>> parameters, ReportModel.ContentType type) {
        this.type = ContentType.PDF;
        this.template = template;
        this.setConnection(connection);
        this.parameters = parameters;
        this.type = type;
    }

    public JasperModel(String template, Connection connection, List<Map<String, Object>> parameters, ReportModel.ContentType type, JasperReport report) {
        this.type = ContentType.PDF;
        this.template = template;
        this.setConnection(connection);
        this.parameters = parameters;
        this.type = type;
        this.report = report;
    }

    public JasperModel(String template, Connection connection, List<Map<String, Object>> parameters, ReportModel.ContentType type, JasperReport report, String title) {
        this.type = ContentType.PDF;
        this.template = template;
        this.setConnection(connection);
        this.parameters = parameters;
        this.type = type;
        this.report = report;
        this.title = title;
    }

    public String getTemplate() {
        return this.template;
    }

    public List<Map<String, Object>> getParameters() {
        return this.parameters;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;

        try {
            connection.setAutoCommit(true);
        } catch (SQLException var3) {
            SQLException e = var3;
            LOGGER.error(e.getMessage(), e);
        }

    }

    public void setParameters(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }

    public ReportModel.ContentType getType() {
        return this.type;
    }

    public void setType(ReportModel.ContentType type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JasperReport getReport() {
        return this.report;
    }

    public void setReport(JasperReport report) {
        this.report = report;
    }

    public boolean needDatasource() {
        JRQuery query = this.report.getQuery();
        return query != null && query.getChunks() != null && query.getChunks().length > 0;
    }
}
