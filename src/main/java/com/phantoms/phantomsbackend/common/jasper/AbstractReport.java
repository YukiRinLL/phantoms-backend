package com.phantoms.phantomsbackend.common.jasper;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AbstractReport {
    public static final String TEMPLATE_URL = "/reports/";
    public static final String TEMPLATE_KEY = "template_code";
    protected static final Log log = LogFactory.getLog(AbstractReport.class);
    protected ReportModel model;
    private HttpServletRequest request;

    public AbstractReport(ReportModel model, HttpServletRequest request) {
        this.model = model;
        this.request = request;
    }

    public byte[] toBytes() {
        ReportModel.ContentType type = this.model.getType();
        byte[] bytes = null;
        switch (type) {
            case HTML:
                bytes = this.toHTML();
                break;
            case PDF:
                bytes = this.toPDF();
                break;
            case EXCEL:
                bytes = this.toEXCEL();
                break;
            case WORD:
                bytes = this.toWORD();
        }

        return bytes;
    }

    protected abstract byte[] toHTML();

    protected abstract byte[] toPDF();

    protected abstract byte[] toEXCEL();

    protected abstract byte[] toWORD();

    public HttpServletRequest getRequest() {
        return this.request;
    }
}
