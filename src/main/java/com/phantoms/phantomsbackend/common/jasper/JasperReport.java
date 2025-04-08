package com.phantoms.phantomsbackend.common.jasper;

import jakarta.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JasperReport extends AbstractReport{
    public JasperReport(ReportModel model, HttpServletRequest request) {
        super(model, request);
    }

    public byte[] toPDF() {
        try {
            List<JasperPrint> jasperPrint = this.prepareJasperPrint();
            JRPdfExporter pdfexporter = new JRPdfExporter();
            pdfexporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrint);
            ByteArrayOutputStream bout = new ByteArrayOutputStream(1048576);
            pdfexporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bout);
            pdfexporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.FALSE);
            pdfexporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
            Long startTime = System.currentTimeMillis();
            pdfexporter.exportReport();
            if (log.isInfoEnabled()) {
                log.info("jasper执行导出为pdf流耗时[" + (System.currentTimeMillis() - startTime) / 1000L + "]秒");
            }

            bout.flush();
            byte[] bytes = bout.toByteArray();
            bout.close();
            return bytes;
        } catch (Exception var6) {
            Exception e = var6;
            log.error(e.getMessage(), e);

            throw new ReportException("JRException:::Export PDF Report ERROR: Template:" + this.model.getTemplate() + e.getMessage());
        }
    }

    private List<JasperPrint> prepareJasperPrint() throws JRException {
        List<JasperPrint> jasperPrints = new ArrayList();
        Long startTime = System.currentTimeMillis();
        Iterator var4 = this.model.getParameters().iterator();

        while(var4.hasNext()) {
            Map<String, Object> parameterMap = (Map)var4.next();
            if (this.model.needDatasource()) {
                jasperPrints.add(JasperFillManager.fillReport(this.model.getTemplate(), parameterMap, this.model.getConnection()));
            } else {
                jasperPrints.add(JasperFillManager.fillReport(this.model.getTemplate(), parameterMap, new JREmptyDataSource()));
            }
        }

        if (log.isInfoEnabled()) {
            log.info("jasper从数据库中准备数据耗时[" + (System.currentTimeMillis() - startTime) / 1000L + "]秒");
        }

        return jasperPrints;
    }

    protected byte[] toEXCEL() {
        try {
            List<JasperPrint> jasperPrint = this.prepareJasperPrint();
            JRExporter jrXlsExporter = new JRXlsExporter();
            jrXlsExporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrint);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            jrXlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bout);
            jrXlsExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            jrXlsExporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
            Long startTime = System.currentTimeMillis();
            jrXlsExporter.exportReport();
            if (log.isInfoEnabled()) {
                log.info("jasper执行导出为Excel流耗时[" + (System.currentTimeMillis() - startTime) / 1000L + "]秒");
            }

            bout.flush();
            byte[] bytes = bout.toByteArray();
            bout.close();
            return bytes;
        } catch (Exception var6) {
            Exception e = var6;
            log.error(e.getMessage(), e);
            throw new ReportException("JRException:::Export EXCEL Report ERROR: Template:" + this.model.getTemplate() + e.getMessage());
        }
    }

    public byte[] toHTML() {
        try {
            List<JasperPrint> jasperPrint = this.prepareJasperPrint();
            JRExporter exporter = new HtmlExporter();
            this.getRequest().getSession().setAttribute("net.sf.jasperreports.j2ee.jasper_print", jasperPrint);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrint);
            StringWriter writer = new StringWriter();
            exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, writer);
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "servlets/image?image=");
            exporter.setParameter(JRHtmlExporterParameter.SIZE_UNIT, JRHtmlExporterParameter.SIZE_UNIT);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
            Long startTime = System.currentTimeMillis();
            exporter.exportReport();
            if (log.isInfoEnabled()) {
                log.info("jasper执行导出为Html流耗时[" + (System.currentTimeMillis() - startTime) / 1000L + "]秒");
            }

            writer.flush();
            byte[] bytes = writer.toString().getBytes(Charset.forName("UTF-8"));
            writer.close();
            return bytes;
        } catch (Exception var6) {
            Exception e = var6;
            log.error(e.getMessage(), e);
            throw new ReportException("JRException:::Export HTML Report ERROR: Template:" + this.model.getTemplate() + e.getMessage());
        }
    }

    public byte[] toWORD() {
        try {
            List<JasperPrint> jasperPrint = this.prepareJasperPrint();
            JRExporter exporter = new JRDocxExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrint);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bout);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
            Long startTime = System.currentTimeMillis();
            exporter.exportReport();
            if (log.isInfoEnabled()) {
                log.info("jasper执行导出为Word流耗时[" + (System.currentTimeMillis() - startTime) / 1000L + "]秒");
            }

            bout.flush();
            byte[] bytes = bout.toByteArray();
            bout.close();
            return bytes;
        } catch (Exception var6) {
            Exception e = var6;
            log.error(e.getMessage(), e);
            throw new ReportException("JRException:::Export WORD Report ERROR: Template:" + this.model.getTemplate() + e.getMessage());
        }
    }
}
